package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendUserServiceImp implements RecommendUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findRecommendedUsers(int userId) {
        String userClusterId=userMapper.getUserClusterIdById(userId);
        if(userClusterId==null){
            throw new IllegalArgumentException("用户聚类ID不存在");
        }

        //解析聚类ID
        Integer primaryClusterId=getPrimaryClusterId(userClusterId);
        System.out.println("Parsed primaryClusterId: " + primaryClusterId);

        //查询相似用户
        List<User> similarUsers=userMapper.findUsersByClusterId(primaryClusterId,userId,5);
        System.out.println("ClusterId: " + primaryClusterId);
        System.out.println("ExcludeUserId: " + userId);
        System.out.println("Similar Users: " + similarUsers);
        return similarUsers;
    }

    public Integer getPrimaryClusterId(String userClusterId){
        if(userClusterId==null||userClusterId.isEmpty()){
            return null;
        }
        String[] clusterIds = userClusterId.split(",");
        return Integer.valueOf(clusterIds[0].trim());
    }
}
