package com.moesee.moeseedemo.mapper;

import com.moesee.moeseedemo.pojo.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper {


    int getVideoClusterIdByUrl(@Param("videoUrl") String videoUrl);


    List<Video> findVideosByClusterId (@Param("clusterId") Integer clusterId,
                                       @Param("videoUrl") String videoUrl,
                                       @Param("limit") Integer limit);
}
