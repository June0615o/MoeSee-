package com.moesee.moeseedemo.controller;

import com.moesee.moeseedemo.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @PostMapping("/predict")
    public ResponseEntity<Map<String,Object>> predictVideoHeat(@RequestParam String videoUrl,@RequestParam String date){
        Map<String,Object> result = analyticsService.analyzeVideoHeat(videoUrl,date); //bv号,"yy-mm-dd"格式日期
        return ResponseEntity.ok(result);
    }
}
