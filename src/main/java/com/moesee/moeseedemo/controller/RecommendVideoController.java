package com.moesee.moeseedemo.controller;

import com.moesee.moeseedemo.pojo.Video;
import com.moesee.moeseedemo.service.RecommendVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendVideoController {

    @Autowired
    private RecommendVideoService recommendVideoService;

    @GetMapping("/recommendvideos")
    public ResponseEntity<List<Video>> recommendVideos(@RequestParam String videoUrl ){ //bv号
        List<Video> recommendedVideos = recommendVideoService.findRecommendVideos(videoUrl);
        return ResponseEntity.ok(recommendedVideos);
    }
}
