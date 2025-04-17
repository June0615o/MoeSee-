package com.moesee.moeseedemo.controller;

import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody Map<String,String> loginRequest){
        String userAccount=loginRequest.get("userAccount");
        String password=loginRequest.get("password");

        Integer userId=userMapper.validateUser(userAccount,password);
        Integer userUid=userMapper.getUserUidById(userId);
        if (userId == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }
        //生成JWT
        String token = JWTUtils.generateToken(userUid.toString());
        String redisKey= "cache:auth:token:"+userUid;
        System.out.println("针对用户:"+userUid+"的JWT已生成:"+token);
        //存储在redis中
        stringRedisTemplate.opsForValue().set(redisKey,token, Duration.ofDays(1));
        //返回令牌
        Map<String,String> response = new HashMap<>();
        response.put("token",token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String,String> request){
        String phone=request.get("phone");
        //验证手机号是否合法
        if(!phone.matches("^1[3-9]\\d{9}$")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("手机号不合法");
        }
        //用redis来检查请求频率与数据有效期(60s内不能重复发送)
        String redisKey="cache:register:data:"+phone;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))){
            Map<Object,Object> existingData=stringRedisTemplate.opsForHash().entries(redisKey);
            if(existingData!=null && !existingData.isEmpty() && existingData.containsKey("sentTime")){
                long lastSentTime=Long.parseLong(existingData.get("sentTime").toString());
                if(System.currentTimeMillis()-lastSentTime<60000){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("验证码请求过于频繁，请稍后再尝试");
                }
            }
        }
        String code = String.valueOf(new Random().nextInt(900000)+100000);
        Map<String,String> data=new HashMap<>();
        data.put("phone",phone);
        data.put("code",code);
        data.put("sentTime",String.valueOf(System.currentTimeMillis()));
        stringRedisTemplate.opsForHash().putAll(redisKey,data);
        stringRedisTemplate.expire(redisKey,Duration.ofMinutes(5));
        System.out.println("验证码已发送:"+code);

        return ResponseEntity.ok("验证码已发送,"+code);
    }
    @PostMapping("/mblogin")
    public ResponseEntity<?> mblogin(@RequestBody Map<String,String> request){
        String phone=request.get("phone");
        String code = request.get("code");

        String redisKey="cache:register:data:"+phone;
        Map<Object,Object> existingData=stringRedisTemplate.opsForHash().entries(redisKey);
        /*
         必须: 登录的手机号和之前发送的手机号匹配; 验证码必须匹配; 验证码在有效时间内.
         */
        System.out.println("Debug - Redis Data: " + existingData);

        String redisCode=existingData.get("code").toString();
        if(existingData==null || existingData.isEmpty() || !redisCode.equals(code)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("验证码错误或已过期");
        }
        //验证通过,进行后续逻辑
        Integer userId=userMapper.getUserIdByPhone(phone);
        /*
        如果用户注册过,那么userId就不为null.以检查null为核心逻辑
        如果非null,返回JWT.(以userUid为密钥生成)
        如果为null,进入设置密码的业务逻辑,并且保存其user_account和user_password字段
        然后,再进入选择聚落的界面.
        需要注意的是: 设置密码的业务逻辑完成之后,不设置其userId,
        而是直接跳转到聚落选择的业务,因为userId/userUid的分发与聚落选择高度绑定.
        需要记得更新聚落选择的业务:在返回userUid的同时,也要记得更新user_account中的user_id字段.
         */
        if(userId!=null){
            Integer userUid=userMapper.getUserUidById(userId);
            String token= JWTUtils.generateToken(userUid.toString());
            stringRedisTemplate.opsForValue().set("cache:auth:token:"+userUid,token,Duration.ofDays(1));
            return ResponseEntity.ok(Map.of("token",token));
        }
        else {
            boolean isSetup = false;
            return ResponseEntity.ok(Map.of(
                    "message","手机号未注册,请输入密码以完成注册.",
                    "nextStep","/api/auth/setPassword",
                    "isSetup",isSetup
            ));
        }
    }
    @PostMapping("/setPassword")
    public ResponseEntity<?> setPassword(@RequestBody Map<String,String> request){
        String phone =request.get("phone");
        String password = request.get("password");
        if(password==null||password.length()<6){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("密码长度必须大于 6 位");
        }
        userMapper.insertUserAccount(phone,password);
        System.out.println("新用户注册成功,手机号(账号):"+phone);
        return ResponseEntity.ok(Map.of(
                "message","密码设置成功,请选择您感兴趣的词条.",
                "nextStep","/api/register"
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(7);
            Claims claims = JWTUtils.parseToken(token);
            String userId = claims.getSubject();

            // 删除 Redis 中的令牌
            String redisKey = "auth:token:" + userId;
            stringRedisTemplate.delete(redisKey);

            return ResponseEntity.ok("已成功登出");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("无效的令牌");
    }

}
