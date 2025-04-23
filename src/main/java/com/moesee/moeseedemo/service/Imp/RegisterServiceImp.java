package com.moesee.moeseedemo.service.Imp;

import com.moesee.moeseedemo.dto.RegisterDTO;
import com.moesee.moeseedemo.mapper.UserMapper;
import com.moesee.moeseedemo.pojo.User;
import com.moesee.moeseedemo.service.RecommendUserService;
import com.moesee.moeseedemo.service.RecommendVideoForUserService;
import com.moesee.moeseedemo.service.RegisterService;
import com.moesee.moeseedemo.utils.ClusterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RegisterServiceImp implements RegisterService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RecommendVideoForUserService recommendVideoForUserService;
    @Autowired
    private RecommendUserService recommendUser;

    @Override
    public int registerUser(String phone,List<Integer> clusterIds) {
        String userClusterId = clusterIds.stream()
                .map(String::valueOf) // 将 Integer 转换为 String
                .collect(Collectors.joining(",")); // 以逗号拼接

        String userPreferredTags = clusterIds.stream()
                .map(ClusterUtils::clusterIds2Name) // 翻译为中文名称
                .collect(Collectors.joining(",")); // 以逗号拼接

        int userUid = userMapper.getMaxUserUid() + 1;
        userMapper.registerUser(userUid, userPreferredTags, userClusterId);
        int userId = userMapper.getUserIdByUid(userUid);
        userMapper.addUserId(userId,phone);
        System.out.println("用户注册成功,uid为"+userUid);
        /*
        System.out.println("正在推荐首次访问所需的16个推荐视频以及4个用户");

        List<Map<String,Object>> recommendedVideos=recommendVideoForUserService.recommendVideosForUser(userUid,16);
        List<User> recommendedUsers=recommendUser.findRecommendedUsers(userUid);
        RegisterDTO registerDTO=new RegisterDTO(userUid,recommendedVideos,recommendedUsers);

        return registerDTO;
        */
        return userUid;
    }
}
