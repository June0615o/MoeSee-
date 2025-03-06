package com.moesee.moeseedemo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element; // 确保导入 Element 类
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VideoScraperServiceImpl implements VideoScraperService {
    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${scraper.url}")
    private String scraperUrl;

    @Override
    public void scrapeVideos() {
        try {
            // 获取网页文档
            Document document = Jsoup.connect(scraperUrl).get();

            // 提取视频卡片信息
            Elements videoCards = document.select("div.bili-video-card__wrap"); // 根据B站的实际HTML结构选择合适的CSS选择器
            for (Element videoCard : videoCards) {
                String title = videoCard.select("h3.bili-video-card__info--tit a").text(); // 获取标题文本内容
                String videoUrl = videoCard.select("h3.bili-video-card__info--tit a").attr("href"); // 获取视频链接

                // 获取播放量信息并转换为整数
                String viewsText = videoCard.select("span.bili-video-card__stats--text").text();
                int views = viewsText.isEmpty() ? 0 : convertViewsToInt(viewsText.replaceAll("[^\\d.万]", ""));

                // 获取视频时长信息并转换为秒数
                String durationText = videoCard.select("span.bili-video-card__stats__duration").text();
                int duration = durationText.isEmpty() ? 0 : convertDurationToSeconds(durationText);

                // 打印视频信息
                System.out.println("Title: " + title);
                System.out.println("URL: " + videoUrl);
                System.out.println("Views: " + views);
                System.out.println("Duration: " + duration + " seconds");
                System.out.println("-----------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将播放量字符串转换为整数
    private static int convertViewsToInt(String viewsText) {
        // 处理带有"万"字的情况
        if (viewsText.contains("万")) {
            double viewsDouble = Double.parseDouble(viewsText.replace("万", "")) * 10000;
            return (int) viewsDouble;
        }
        return Integer.parseInt(viewsText.replaceAll("[^\\d]", ""));
    }

    // 将时长字符串转换为秒数
    private static int convertDurationToSeconds(String durationText) {
        String[] parts = durationText.split(":");
        int seconds = 0;
        if (parts.length == 2) {
            seconds = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        } else if (parts.length == 3) {
            seconds = Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
        }
        return seconds;
    }

    private void saveVideoToDatabase(String title, String tags, String videoUrl, int views, int duration, String uploadDate) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "INSERT INTO videos (video_title, video_tags, video_url, video_views, video_duration, video_upload_date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, tags);
            statement.setString(3, videoUrl);
            statement.setInt(4, views);
            statement.setInt(5, duration);
            statement.setString(6, uploadDate);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}