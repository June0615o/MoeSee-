package com.moesee.moeseedemo.controller;

import com.moesee.moeseedemo.service.RecommendVideoForUserService;
import com.moesee.moeseedemo.service.RecommendVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class RecommendController {
    @Autowired
    private RecommendVideoForUserService recommendVideoForUserService;

    @GetMapping("/recommend")
    public ResponseEntity<List<Map<String,Object>>> recommendVideos(@RequestParam Integer userUid){
        List<Map<String,Object>> recommendedVideos= recommendVideoForUserService.recommendVideosForUser(userUid,4);
        return ResponseEntity.ok(recommendedVideos);
    }

    @GetMapping("/firstrecommend")
    public ResponseEntity<List<Map<String,Object>>> firstRecommendVideos(@RequestParam Integer userUid){
        List<Map<String,Object>> recommendedVideos= recommendVideoForUserService.recommendVideosForUser(userUid,16);
        return ResponseEntity.ok(recommendedVideos);
    }
}
