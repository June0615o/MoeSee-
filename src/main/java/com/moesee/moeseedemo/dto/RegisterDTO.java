package com.moesee.moeseedemo.dto;

import com.moesee.moeseedemo.pojo.User;

import java.util.List;
import java.util.Map;

public class RegisterDTO {
    private int userUid;
    private String token;

    public int getUserUid() {
        return userUid;
    }

    public void setUserUid(int userUid) {
        this.userUid = userUid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
