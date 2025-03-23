package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.mapper.VideoMapper;
import com.moesee.moeseedemo.pojo.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendVideoServiceImp implements RecommendVideoService{

    @Autowired
    private VideoMapper videoMapper;

    @Override
    public List<Video> findRecommendVideos(String videoUrl) {
        int videoClusterId = videoMapper.getVideoClusterIdByUrl(videoUrl); //通过视频url查找其聚类id
        System.out.println("videoClusterId: " + videoClusterId);
        if(videoClusterId == -1){
            throw new IllegalArgumentException("用户聚类ID不存在或暂时无法解析");
        }
        List<Video> recommendVideos = videoMapper.findVideosByClusterId(videoClusterId, videoUrl, 5);
        return recommendVideos;
    }
}
