package com.moesee.moeseedemo.service;

import java.util.Map;

public interface AnalyticsService {
    Map<String,Object> analyzeVideoHeat(String videoUrl,String date);
}
