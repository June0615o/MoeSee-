package com.moesee.moeseedemo.controller;


import com.moesee.moeseedemo.dto.RegisterDTO;
import com.moesee.moeseedemo.service.RegisterService;
import com.moesee.moeseedemo.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    public ResponseEntity<RegisterDTO> registerUser(@RequestBody Object requestBody){
        List<Integer> clusterIds;
        if (requestBody instanceof Map) { // 处理 {tags: [1,5,9]} 格式
            Map<String, Object> body = (Map<String, Object>) requestBody;
            clusterIds = (List<Integer>) body.get("tags");
        } else { // 处理直接的 [1,5,9] 格式
            clusterIds = (List<Integer>) requestBody;
        }
        RegisterDTO registerDTO = new RegisterDTO();
        int userUid=registerService.registerUser(clusterIds);
        String token = JWTUtils.generateToken(String.valueOf(userUid));
        stringRedisTemplate.opsForValue().set("cache:auth:token:"+userUid,token, Duration.ofDays(1));
        registerDTO.setUserUid(userUid);
        registerDTO.setToken(token);
        return ResponseEntity.ok(registerDTO);
    }
}
