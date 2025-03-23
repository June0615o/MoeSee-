package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.pojo.Video;

import java.util.List;

public interface RecommendVideoService {
    List<Video> findRecommendVideos(String videoUrl);
}
