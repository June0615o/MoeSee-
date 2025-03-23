package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.pojo.Video;

public interface FindVideosByClusterIdRandomlyWeighted {
    Video findVideosByClusterIdRandomlyWeighted(int clusterId);
}
