package com.moesee.moeseedemo.service.Imp;

import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.mapper.VideoMapper;
import com.moesee.moeseedemo.pojo.Video;
import com.moesee.moeseedemo.service.MockUserWatchingService;
import com.moesee.moeseedemo.utils.ClusterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class MockUserWatchingServiceImp implements MockUserWatchingService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private com.moesee.moeseedemo.service.FindVideosByClusterIdRandomlyWeighted FindVideosByClusterIdRandomlyWeighted;

    private Random random = new Random();
    @Override
    public void mockUserWatching() {
        int userId = generateRandomUserId();
        String userClusterId=userMapper.getUserClusterIdById(userId);

        if (userClusterId == null || userClusterId.isEmpty()) {
            System.out.println("对应的用户userId未查询到对应的聚类Id");
            return;
        }

        List<Integer> clusterIds = ClusterUtils.parseClusterIds(userClusterId);
        if(clusterIds.isEmpty()){
            System.out.println("对应的用户userId的聚类Id解析不正确");
            return;
        }

        int clusterId=clusterIds.get(random.nextInt(clusterIds.size())); //在用户的聚类id集合中随机选取一个

        Video selectedVideo = FindVideosByClusterIdRandomlyWeighted.findVideosByClusterIdRandomlyWeighted(clusterId);
        if (selectedVideo == null){
            System.out.println("未找到对应的视频");
            return;
        }

        LocalDate currentDate=LocalDate.now();
        videoMapper.insertViewHistory(userId, selectedVideo.getVideoId(), currentDate);
        videoMapper.incrementVideoViews(selectedVideo.getVideoId());

        System.out.println("用户userId: " + userId + " 观看了视频: " + selectedVideo.getVideoTitle()+
                ", 这个视频的id为"+ selectedVideo.getVideoId());
        System.out.println("此次用户点赞的概率为"+ generateLikeProbability());

        if(random.nextDouble()<generateLikeProbability()){
            videoMapper.incrementVideoLikes(selectedVideo.getVideoId());
            videoMapper.insertLikeHistory(userId, selectedVideo.getVideoId(), currentDate);
            System.out.println("用户此次点赞了视频.");
        }
        else{
            System.out.println("用户此次未点赞视频.");
        }
    }

    private int generateRandomUserId(){
        return random.nextInt(10000)+1;
    }

    private double generateLikeProbability(){
        return 0.3+random.nextDouble()*0.4;
    }
}
