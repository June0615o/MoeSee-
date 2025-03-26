package com.moesee.moeseedemo.mapper;

import com.moesee.moeseedemo.pojo.Video;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {

    int getVideoIdByUrl(@Param("videoUrl") String videoUrl);
    int getVideoClusterIdByUrl(@Param("videoUrl") String videoUrl);

    List<Video> findVideosByClusterId (@Param("clusterId") Integer clusterId,
                                       @Param("videoUrl") String videoUrl,
                                       @Param("limit") Integer limit);

    void insertViewHistory(@Param("userId") Integer userId,
                           @Param("videoId") Integer videoId,
                           @Param("date") LocalDate date);
    void insertLikeHistory(@Param("userId") Integer userId,
                           @Param("videoId") Integer videoId,
                           @Param("date") LocalDate date);
    /*

    插入观看记录. 请注意使用时注意
    调用下面incrementVideoViews方法将对应视频播放量字段video_views +1
    插入点赞记录同理。

    */

    void incrementVideoViews(@Param("videoId") Integer videoId);
    void incrementVideoLikes(@Param("videoId") Integer videoId);

    int getVideoViewsByVideoIdAndDate(@Param("videoId") Integer videoId,
                                      @Param("date")LocalDate date);
    int getVideoLikesByVideoIdAndDate(@Param("videoId") Integer videoId,
                                      @Param("date")LocalDate date);

    List<Integer> getLikedVideosByUserId(@Param("userId") Integer userId);

    Video getVideoDetailsById(@Param("videoId") Integer videoId);

}
