package com.moesee.moeseedemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

@Service
public class RandomUserDataGenerateServiceImp implements RandomUserDataGenerateService {

    private static final String[] TAGS = {
            "番剧", "国创", "综艺", "动画", "鬼畜", "舞蹈", "娱乐", "科技数码", "美食", "汽车",
            "体育运动", "电影", "电视剧", "纪录片", "游戏", "音乐", "影视", "知识", "资讯", "小剧场",
            "时尚美妆", "动物", "vlog", "绘画", "人工智能", "家装房产", "户外", "健身", "手工",
            "旅游出行", "三农", "亲子", "健康", "情感", "生活兴趣", "生活经验", "公益"
    };

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public void generateRandomUserData() {
        int numUsers = 10;
        int initialUserId = 100000;
        Random random = new Random();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "INSERT INTO users (user_uid, user_preferred_tags, user_behavior_history) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int userId = 1; userId <= numUsers; userId++) {
                    int userUid = initialUserId + userId;
                    String preferredTags = getRandomTags(random);
                    int behaviorHistory = userId;

                    pstmt.setInt(1, userUid);
                    pstmt.setString(2, preferredTags);
                    pstmt.setInt(3, behaviorHistory);

                    pstmt.executeUpdate();

                    // 输出到控制台
                    System.out.println("Inserted user_uid:" + userUid + " preferred_tags:" + preferredTags + " behavior_history:" + behaviorHistory);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getRandomTags(Random random) {
        int numTags = random.nextInt(2) + 1;
        StringBuilder tags = new StringBuilder();
        for (int i = 0; i < numTags; i++) {
            if (i > 0) {
                tags.append(",");
            }
            tags.append(TAGS[random.nextInt(TAGS.length)]);
        }
        return tags.toString();
    }
}
