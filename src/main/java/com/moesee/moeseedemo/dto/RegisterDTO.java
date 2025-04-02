package com.moesee.moeseedemo.dto;

import com.moesee.moeseedemo.pojo.User;

import java.util.List;
import java.util.Map;

public class RegisterDTO {
    private int userUid;
    private List<Map<String,Object>> recommendedVideos;
    private List<User> recommendedUsers;

    public int getUserUid() {
        return userUid;
    }

    public void setUserUid(int userUid) {
        this.userUid = userUid;
    }

    public List<Map<String, Object>> getRecommendedVideos() {
        return recommendedVideos;
    }

    public void setRecommendedVideos(List<Map<String, Object>> recommendedVideos) {
        this.recommendedVideos = recommendedVideos;
    }

    public List<User> getRecommendedUsers() {
        return recommendedUsers;
    }

    public RegisterDTO(int userUid, List<Map<String, Object>> recommendedVideos, List<User> recommendedUsers) {
        this.userUid = userUid;
        this.recommendedVideos = recommendedVideos;
        this.recommendedUsers = recommendedUsers;
    }

    public RegisterDTO() {
    }

    public void setRecommendedUsers(List<User> recommendedUsers) {
        this.recommendedUsers = recommendedUsers;
    }
}
