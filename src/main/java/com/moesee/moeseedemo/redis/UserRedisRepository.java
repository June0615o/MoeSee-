package com.moesee.moeseedemo.redis;

import com.moesee.moeseedemo.pojo.Video;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class UserRedisRepository {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void saveLikedVideoToRedis(int userUid, int videoId){
        String key= "cache:user_liked_videos:"+userUid;
        stringRedisTemplate.opsForSet().add(key,String.valueOf(videoId));
    }
    public void saveVideoDetailsToRedis(Video video,long timeoutInDays){
        String redisKey="cache:video_details:"+video.getVideoId();
        if(!stringRedisTemplate.hasKey(redisKey)) {
            stringRedisTemplate.opsForHash().put(redisKey, "VideoCoverImage", video.getVideoCoverImage());
            stringRedisTemplate.opsForHash().put(redisKey, "videoUrl", video.getVideoUrl());
            stringRedisTemplate.opsForHash().put(redisKey, "videoTitle", video.getVideoTitle());
            //设置过期时间
            stringRedisTemplate.expire(redisKey, Duration.ofDays(timeoutInDays));
            System.out.println("保存视频详情到redis成功,键:" + redisKey);
        }
        else {
            stringRedisTemplate.expire(redisKey, Duration.ofDays(timeoutInDays));
            System.out.println("视频详情已存在redis中,键:" + redisKey+
                    "已经刷新了其有效期.");
        }
    }
}
