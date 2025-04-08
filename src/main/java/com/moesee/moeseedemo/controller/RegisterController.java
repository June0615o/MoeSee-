package com.moesee.moeseedemo.controller;


import com.moesee.moeseedemo.dto.RegisterDTO;
import com.moesee.moeseedemo.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public ResponseEntity<Integer> registerUser(@RequestBody Object requestBody){
        List<Integer> clusterIds;
        if (requestBody instanceof Map) { // 处理 {tags: [1,5,9]} 格式
            Map<String, Object> body = (Map<String, Object>) requestBody;
            clusterIds = (List<Integer>) body.get("tags");
        } else { // 处理直接的 [1,5,9] 格式
            clusterIds = (List<Integer>) requestBody;
        }
        int userUid=registerService.registerUser(clusterIds);
        return ResponseEntity.ok(userUid);
    }
}
