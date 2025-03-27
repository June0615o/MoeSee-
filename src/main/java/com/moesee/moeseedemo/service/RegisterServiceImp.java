package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.utils.ClusterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RegisterServiceImp implements RegisterService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public int registerUser(List<Integer> clusterIds) {
        String userClusterId = clusterIds.stream()
                .map(String::valueOf) // 将 Integer 转换为 String
                .collect(Collectors.joining(",")); // 以逗号拼接

        String userPreferredTags = clusterIds.stream()
                .map(ClusterUtils::clusterIds2Name) // 翻译为中文名称
                .collect(Collectors.joining(",")); // 以逗号拼接

        int userUid = userMapper.getMaxUserUid() + 1;
        userMapper.registerUser(userUid, userPreferredTags, userClusterId);
        System.out.println("用户注册成功,uid为"+userUid);
        return userUid;
    }
}
