package com.moesee.moeseedemo.controller;

import com.moesee.moeseedemo.pojo.User;
import com.moesee.moeseedemo.service.RecommendUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendUserController {

    @Autowired
    private RecommendUserService recommendUserService;

    @GetMapping("/recommendusers")
    public ResponseEntity<List<User>> recommendUsers(@RequestParam int userUid){
        List<User> recommendedUsers = recommendUserService.findRecommendedUsers(userUid);
        return ResponseEntity.ok(recommendedUsers);
    }
}
