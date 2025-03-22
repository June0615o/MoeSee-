package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.pojo.User;

import java.util.List;

public interface RecommendUserService {
    List<User> findRecommendedUsers(int userId);
}
