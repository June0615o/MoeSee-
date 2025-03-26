package com.moesee.moeseedemo.service;

import java.util.List;
import java.util.Map;

public interface RecommendVideoForUserService {
    List<Map<String,Object>> recommendVideosForUser(Integer userUid);
}
