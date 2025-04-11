package com.moesee.moeseedemo.service.Imp;

import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.mapper.VideoMapper;
import com.moesee.moeseedemo.pojo.Video;
import com.moesee.moeseedemo.redis.UserRedisRepository;
import com.moesee.moeseedemo.service.AnalyticsService;
import com.moesee.moeseedemo.service.RecommendVideoForUserService;
import com.moesee.moeseedemo.utils.ClusterUtils;
import com.moesee.moeseedemo.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class RecommendVideoForUserServiceImp implements RecommendVideoForUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ClusterUtils clusterUtils;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private DateUtils dateUtils;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserRedisRepository userRedisRepository;

    @Override
    public List<Map<String,Object>> recommendVideosForUser(Integer userUid,int count) {
        int userId = userMapper.getUserIdByUid(userUid);
        String userClusterId = userMapper.getUserClusterIdById(userId);
        List<Integer> clusterIds = clusterUtils.parseClusterIds(userClusterId);

        List<Integer> recommendVideoIds = new ArrayList<>();
        Set<Integer> processedUsers = new HashSet<>();
        processedUsers.add(userId); //避免推荐自己点赞的视频

        /*
        根据[优先匹配原则]&[优先喜爱原则]查找用户及其点赞的视频
        [优先匹配原则]: 优先以聚类最多与用户相似的用户为准
        [优先喜爱原则]: 优先以相似用户点赞的视频为推荐标准
         */

        List<Integer> similarUsersforRecommend = userMapper.getUsersByExactClusterIds(clusterIds, userId);
        recommendVideoIds.addAll(getVideosFromSimilarUsers(similarUsersforRecommend, processedUsers, count));

        //如果未能满足完全一致的聚类ID相似需求且推荐视频列表少于count(所需)，逐步降低匹配标准
        if (recommendVideoIds.size() < count) {
            for (Integer clusterId : clusterIds) {
                similarUsersforRecommend = userMapper.getUsersByClusterId(clusterId, userId);
                recommendVideoIds.addAll(getVideosFromSimilarUsers(similarUsersforRecommend, processedUsers, count));
                if (recommendVideoIds.size() >= count) break;
            }
        }
        List<Map<String, Object>> recommendVideoDetails = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        String dateStr = dateUtils.localDateToString(localDate);
        Video videoDetails = null;
        for (Integer videoId : recommendVideoIds) {
            String redisKey = "cache:video_details:" + videoId;
            //优先从Redis中获取视频详情
            Map<Object, Object> videoDetailsFromCache = stringRedisTemplate.opsForHash().entries(redisKey);
            if (videoDetailsFromCache != null && !videoDetailsFromCache.isEmpty()) {
                //缓存命中.
                videoDetails = new Video();
                videoDetails.setVideoId(videoId);
                videoDetails.setVideoCoverImage((String) videoDetailsFromCache.get("VideoCoverImage"));
                videoDetails.setVideoUrl((String) videoDetailsFromCache.get("videoUrl"));
                videoDetails.setVideoTitle((String) videoDetailsFromCache.get("videoTitle"));
                String videoUrl = videoDetails.getVideoUrl();
                Map<String, Object> analyzeResult = analyticsService.analyzeVideoHeat(videoUrl, dateStr);
                videoDetails.setVideoHeat((double) analyzeResult.get("futureHeat:"));
                videoDetails.setVideoFutureViews((int) analyzeResult.get("futureViews:"));
                System.out.println("从Redis中获取了视频详细信息.视频ID" + videoId);
            } else {
                //缓存未命中.从MySQL中获取详情并写回Redis.
                videoDetails = videoMapper.getVideoDetailsById(videoId);
                String videoUrl = videoDetails.getVideoUrl();
                Map<String, Object> analyzeResult = analyticsService.analyzeVideoHeat(videoUrl, dateStr);
                videoDetails.setVideoHeat((double) analyzeResult.get("futureHeat:"));
                videoDetails.setVideoFutureViews((int) analyzeResult.get("futureViews:"));
                if (videoDetails != null) {
                    userRedisRepository.saveVideoDetailsToRedis(videoDetails, 20);
                    System.out.println("从 MySQL 查询视频详细信息并存入 Redis，视频 ID：" + videoId);
                }
            }
            System.out.println("调试：视频详细信息 -> " + videoDetails);
            if (videoDetails != null) {
                if (videoDetails.getVideoTitle() != null &&
                        videoDetails.getVideoUrl() != null &&
                        videoDetails.getVideoCoverImage() != null) {
                    Map<String, Object> videoDetailMap = new HashMap<>();
                    videoDetailMap.put("videoTitle", videoDetails.getVideoTitle());
                    videoDetailMap.put("videoUrl", videoDetails.getVideoUrl());
                    videoDetailMap.put("videoCoverImage", videoDetails.getVideoCoverImage());
                    videoDetailMap.put("videoFutureViews", videoDetails.getVideoFutureViews());
                    videoDetailMap.put("videoHeat", videoDetails.getVideoHeat());
                    recommendVideoDetails.add(videoDetailMap);
                } else {
                    System.out.println("警告：视频详细信息包含 null 值，视频 ID：" + videoDetails.getVideoId());
                }
            }
        }
        return recommendVideoDetails;
    }
    private List<Integer> getVideosFromSimilarUsers(List<Integer> similarUsersforRecommend , Set<Integer> processedUsers,int count){
        List<Integer> videoIds=new ArrayList<>();
        Collections.shuffle(similarUsersforRecommend);
        for(Integer similarUserId:similarUsersforRecommend) {
            if(videoIds.size()>=count) break;
            if (processedUsers.contains(similarUserId)) continue;
            //首先，先试图从Redis中获取用户点赞的视频. 如果未命中用户缓存再请求MySQL.
            int similarUserUid=userMapper.getUserUidById(similarUserId);
            Set<String> likedVideosFromCache=stringRedisTemplate.opsForSet().members("cache:user_liked_videos:" + similarUserUid);
            List<Integer> likedVideos;
            if(likedVideosFromCache!=null&&!likedVideosFromCache.isEmpty()){
                //缓存命中
                likedVideos = new ArrayList<>();
                for(String videoIdStr : likedVideosFromCache){
                    likedVideos.add(Integer.parseInt(videoIdStr));
                }
                System.out.println("命中Redis缓存.用户UID:"+similarUserUid);
            }
            else{
                //若缓存未命中.从db中查询后缓存至redis
                likedVideos=videoMapper.getLikedVideosByUserId(similarUserId);
                for(Integer videoId: likedVideos){
                    stringRedisTemplate.opsForSet().add("cache:user_liked_videos:"+similarUserUid,videoId.toString());
                }
                System.out.println("缓存未命中.用户UID:"+similarUserUid);
            }

            for (Integer videoId : likedVideos) {
                if (!videoIds.contains(videoId)) {
                    videoIds.add(videoId);
                    System.out.println("!*调试语句*! video_id:"+videoId+"已存入videoIds中.");
                    System.out.println("!*调试语句*! 目前的videoIds长度为:"+videoIds.size());
                    if (videoIds.size() >= count) break;
                }
            }
            processedUsers.add(similarUserId);
            if(videoIds.size()>=count) break;
        }
        return videoIds;
    }
}
