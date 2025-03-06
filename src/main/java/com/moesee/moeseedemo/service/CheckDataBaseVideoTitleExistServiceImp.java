package com.moesee.moeseedemo.service;

import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;

public class CheckDataBaseVideoTitleExistServiceImp implements CheckDataBaseVideoTitleExistService{

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;
    @Override
    public boolean checkDataBaseVideoTitleExist(String title) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String checkSql = "SELECT COUNT(*) FROM videos WHERE video_title = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkSql);
            checkStatement.setString(1, title);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
