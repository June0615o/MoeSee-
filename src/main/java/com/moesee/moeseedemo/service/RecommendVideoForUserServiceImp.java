package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.mapper.VideoMapper;
import com.moesee.moeseedemo.pojo.Video;
import com.moesee.moeseedemo.utils.ClusterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendVideoForUserServiceImp implements RecommendVideoForUserService{

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClusterUtils clusterUtils;
    @Autowired
    private VideoMapper videoMapper;

    @Override
    public List<Map<String,Object>> recommendVideosForUser(Integer userUid){
        int userId=userMapper.getUserIdByUid(userUid);
        String userClusterId=userMapper.getUserClusterIdById(userId);
        List<Integer> clusterIds = clusterUtils.parseClusterIds(userClusterId);

        List<Integer> recommendVideoIds=new ArrayList<>();
        Set<Integer> processedUsers = new HashSet<>();
        processedUsers.add(userId); //避免推荐自己点赞的视频

        /*
        根据[优先匹配原则]&[优先喜爱原则]查找用户及其点赞的视频
        [优先匹配原则]: 优先以聚类最多与用户相似的用户为准
        [优先喜爱原则]: 优先以相似用户点赞的视频为推荐标准
         */

        List<Integer> similarUsersforRecommend = userMapper.getUsersByExactClusterIds(clusterIds,userId);
        recommendVideoIds.addAll(getVideosFromSimilarUsers(similarUsersforRecommend,processedUsers));

        //如果未能满足完全一致的聚类ID相似需求且推荐视频列表少于4，逐步降低匹配标准
        if (recommendVideoIds.size()<4){
            for(Integer clusterId:clusterIds) {
                similarUsersforRecommend = userMapper.getUsersByClusterId(clusterId, userId);
                recommendVideoIds.addAll(getVideosFromSimilarUsers(similarUsersforRecommend, processedUsers));
                if (recommendVideoIds.size() >= 4) break;
            }
        }
        List<Map<String,Object>> recommendVideoDetails =new ArrayList<>();
        for(Integer videoId:recommendVideoIds){
            Video videoDetails =videoMapper.getVideoDetailsById(videoId);
            System.out.println("调试：视频详细信息 -> " + videoDetails);
            if (videoDetails != null) {
                if (videoDetails.getVideoTitle() != null &&
                        videoDetails.getVideoUrl() != null &&
                        videoDetails.getVideoCoverImage() != null) {
                    Map<String, Object> videoDetailMap = new HashMap<>();
                    videoDetailMap.put("videoTitle", videoDetails.getVideoTitle());
                    videoDetailMap.put("videoUrl", videoDetails.getVideoUrl());
                    videoDetailMap.put("videoCoverImage", videoDetails.getVideoCoverImage());
                    recommendVideoDetails.add(videoDetailMap);
                } else {
                    System.out.println("警告：视频详细信息包含 null 值，视频 ID：" + videoId);
                }
            }
        }
        return recommendVideoDetails;
    }
    private List<Integer> getVideosFromSimilarUsers(List<Integer> similarUsersforRecommend , Set<Integer> processedUsers){
        List<Integer> videoIds=new ArrayList<>();
        for(Integer similarUserId:similarUsersforRecommend) {
            if(videoIds.size()>=4) break;
            if (processedUsers.contains(similarUserId)) continue;
            List<Integer> likedVideos = videoMapper.getLikedVideosByUserId(similarUserId);
            for (Integer videoId : likedVideos) {
                if (!videoIds.contains(videoId)) {
                    videoIds.add(videoId);
                    System.out.println("!*调试语句*! video_id:"+videoId+"已存入videoIds中.");
                    System.out.println("!*调试语句*! 目前的videoIds长度为:"+videoIds.size());
                    if (videoIds.size() >= 4) break;
                }
            }
            processedUsers.add(similarUserId);
            if(videoIds.size()>=4) break;
        }
        return videoIds;
    }
}
