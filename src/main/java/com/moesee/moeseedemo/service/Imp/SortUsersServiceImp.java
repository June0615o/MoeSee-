package com.moesee.moeseedemo.service.Imp;

import com.moesee.moeseedemo.service.SortUsersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class SortUsersServiceImp implements SortUsersService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Override
    public void sortUsers() {

        // 定义标签到大聚类 ID 的映射
        Map<String, Integer> tagToCluster = new HashMap<>();
        tagToCluster.put("综艺", 0);
        tagToCluster.put("动画", 1);
        tagToCluster.put("鬼畜", 2);
        tagToCluster.put("舞蹈", 3);
        tagToCluster.put("娱乐", 4);
        tagToCluster.put("科技数码", 5);
        tagToCluster.put("美食", 6);
        tagToCluster.put("汽车", 7);
        tagToCluster.put("体育运动", 8);
        tagToCluster.put("电影", 9);
        tagToCluster.put("电视剧", 10);
        tagToCluster.put("纪录片", 11);
        tagToCluster.put("游戏", 12);
        tagToCluster.put("音乐", 13);
        tagToCluster.put("影视", 14);
        tagToCluster.put("知识", 15);
        tagToCluster.put("资讯", 16);
        tagToCluster.put("小剧场", 17);
        tagToCluster.put("时尚美妆", 18);
        tagToCluster.put("动物", 19);
        tagToCluster.put("vlog", 20);
        tagToCluster.put("绘画", 21);
        tagToCluster.put("人工智能", 22);
        tagToCluster.put("家装房产", 23);
        tagToCluster.put("户外", 24);
        tagToCluster.put("健身", 25);
        tagToCluster.put("手工", 26);
        tagToCluster.put("旅游出行", 27);
        tagToCluster.put("三农", 28);
        tagToCluster.put("亲子", 29);
        tagToCluster.put("健康", 30);
        tagToCluster.put("情感", 31);
        tagToCluster.put("生活兴趣", 32);
        tagToCluster.put("生活经验", 33);
        tagToCluster.put("公益", 34);
        tagToCluster.put("番剧", 1); // 番剧映射到动画
        tagToCluster.put("国创", 1); // 国创映射到动画

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            // 查询用户表
            String query = "SELECT user_id, user_preferred_tags FROM users";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // 更新用户聚类 ID
            String updateQuery = "UPDATE users SET user_cluster_id = ? WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String preferredTags = resultSet.getString("user_preferred_tags");

                // 映射用户标签到聚类 ID
                String[] tags = preferredTags.split(",");
                Set<Integer> clusterIds = new HashSet<>();
                for (String tag : tags) {
                    tag = tag.trim();
                    if (tagToCluster.containsKey(tag)) {
                        clusterIds.add(tagToCluster.get(tag));
                    }
                }

                // 将聚类 ID 转换为逗号分隔的字符串
                String clusterIdString = String.join(",", clusterIds.stream().map(String::valueOf).toArray(String[]::new));

                // 更新数据库中的 user_cluster_id
                preparedStatement.setString(1, clusterIdString);
                preparedStatement.setInt(2, userId);
                preparedStatement.executeUpdate();
                System.out.println("Updated user_id: " + userId + " with cluster_ids: " + clusterIdString);
            }

            System.out.println("All user cluster IDs updated successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
