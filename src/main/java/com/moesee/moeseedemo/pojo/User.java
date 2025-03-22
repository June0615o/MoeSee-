package com.moesee.moeseedemo.pojo;

import lombok.Getter;
import lombok.Setter;

public class User {
    private int userId;
    private int userUid;
    private String userPreferredTags;
    private String userClusterId;

    public User() {
    }

    public User(int userId, int userUid, String userPreferredTags, String userClusterId) {
        this.userId = userId;
        this.userUid = userUid;
        this.userPreferredTags = userPreferredTags;
        this.userClusterId = userClusterId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserUid() {
        return userUid;
    }

    public void setUserUid(int userUid) {
        this.userUid = userUid;
    }

    public String getUserPreferredTags() {
        return userPreferredTags;
    }

    public void setUserPreferredTags(String userPreferredTags) {
        this.userPreferredTags = userPreferredTags;
    }

    public String getUserClusterId() {
        return userClusterId;
    }

    public void setUserClusterId(String userClusterId) {
        this.userClusterId = userClusterId;
    }
}
