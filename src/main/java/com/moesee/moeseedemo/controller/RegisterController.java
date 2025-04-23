package com.moesee.moeseedemo.controller;


import com.moesee.moeseedemo.dto.RegisterDTO;
import com.moesee.moeseedemo.service.RegisterService;
import com.moesee.moeseedemo.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private RegisterService registerService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @PostMapping("/register")
    public ResponseEntity<RegisterDTO> registerUser(@RequestBody Map<String,Object> requestBody){
        String phone = (String)requestBody.get("phone");
        List<Integer> clusterIds=(List<Integer>)requestBody.get("tags");
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 返回 400，手机号不合法
        }

        // 检查聚类 ID 列表是否为空
        if (clusterIds == null || clusterIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 返回 400，聚类 ID 列表不能为空
        }

        RegisterDTO registerDTO = new RegisterDTO();

        // 将手机号与聚类 ID 一起传递给注册服务
        int userUid = registerService.registerUser(phone, clusterIds);

        // 生成用户的 Token 并存入 Redis
        String token = JWTUtils.generateToken(String.valueOf(userUid));
        stringRedisTemplate.opsForValue().set("cache:auth:token:" + userUid, token, Duration.ofDays(1));

        // 设置返回值
        registerDTO.setUserUid(userUid);
        registerDTO.setToken(token);

        return ResponseEntity.ok(registerDTO);
    }
}
