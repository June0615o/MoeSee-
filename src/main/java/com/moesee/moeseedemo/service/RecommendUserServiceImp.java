package com.moesee.moeseedemo.service;

import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.pojo.User;
import com.moesee.moeseedemo.utils.ClusterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendUserServiceImp implements RecommendUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findRecommendedUsers(int userUid) {
        int userId=userMapper.getUserIdByUid(userUid);
        String userClusterId=userMapper.getUserClusterIdById(userId);
        if(userClusterId==null){
            throw new IllegalArgumentException("用户聚类ID不存在");
        }

        //解析聚类ID
        List<Integer> clusterIds = ClusterUtils.parseClusterIds(userClusterId);
        System.out.println("Parsed ClusterIds: " + clusterIds);

        Set<User> recommendedUsers =new HashSet<>(); //因为去重性所以使用Set

        //对聚类ID列表中的每个，都查询4个相似用户
        for (Integer clusterId : clusterIds) {
            List<User> similarUsers = userMapper.findUsersByClusterId(clusterId, userId, 4);
            System.out.println("ClusterId: " + clusterId);
            System.out.println("ExcludeUserId: " + userId);
            System.out.println("Similar Users: " + similarUsers);
            recommendedUsers.addAll(similarUsers);
        }
        return new ArrayList<>(recommendedUsers);
    }
}