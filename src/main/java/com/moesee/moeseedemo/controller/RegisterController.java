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

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping
    public ResponseEntity<Integer> registerUser(@RequestBody List<Integer> clusterIds){
        int userUid=registerService.registerUser(clusterIds);
        return ResponseEntity.ok(userUid);
    }
}
