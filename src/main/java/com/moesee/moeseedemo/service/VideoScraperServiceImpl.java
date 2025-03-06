package com.moesee.moeseedemo.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moesee.moeseedemo.pojo.Stat;
import com.moesee.moeseedemo.pojo.Video;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Service
public class VideoScraperServiceImpl implements VideoScraperService {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    // 热门推荐接口基础 URL（不含分页参数）
    @Value("${scraper.jsonUrl}")
    private String jsonUrl; // 如 "https://api.bilibili.com/x/web-interface/popular"

    // 固定参数，ps 固定为 20 以及其他必要参数
    @Value("${scraper.ps}")
    private int ps; // 例如：20

    // 固定的额外参数，可直接在配置中设置，也可写在代码里
    @Value("${scraper.web_location}")
    private String webLocation; // 例如 "333.934"

    @Value("${scraper.w_rid}")
    private String wRid; // 例如 "dcb9ec501a580673adda01ce462e12a4"

    @Value("${scraper.wts}")
    private String wts; // 例如 "1741262398"

    private Gson gson = new Gson();

    @Override
    public void scrapeVideos() {
        int pn = 1;
        while (true) {
            // 构造分页 URL
            String pageUrl = jsonUrl + "?ps=" + ps + "&pn=" + pn + "&web_location=" + webLocation
                    + "&w_rid=" + wRid + "&wts=" + wts;
            System.out.println("Fetching page: " + pn);
            try {
                // 使用 Jsoup 获取接口返回的 JSON 数据
                // 注意必须使用 ignoreContentType(true) 以便读取 JSON 类型数据
                String json = Jsoup.connect(pageUrl)
                        .ignoreContentType(true)
                        .execute()
                        .body();

                // 输出部分 JSON 内容用于调试
                System.out.println("Page " + pn + " JSON: " + (json.length() > 100 ? json.substring(0, 100) + "..." : json));

                // 假设返回 JSON 格式为一个数组形式的结果或者 JSON 对象中有分页结果字段
                // 这里假设直接返回视频对象数组，例如: [ { video1... }, { video2... }, ... ]
                Type videoListType = new TypeToken<List<Video>>() {}.getType();
                List<Video> videos = gson.fromJson(json, videoListType);

                // 如果本页不存在视频数据，则结束翻页
                if (videos == null || videos.isEmpty()) {
                    System.out.println("No more videos found on page " + pn + ". Ending pagination.");
                    break;
                }

                // 遍历本页所有视频
                for (Video video : videos) {
                    // 视频标题
                    String title = video.getTitle();
                    // 视频标签：由 tname 和 tnamev2 合并而成（若第二个不为空）
                    String videoTags = video.getTname();
                    if (video.getTnamev2() != null && !video.getTnamev2().trim().isEmpty()) {
                        videoTags += "," + video.getTnamev2();
                    }
                    // 视频 URL：使用 short_link_v2
                    String videoUrl = video.getShort_link_v2();
                    // 播放量：取 stat.view
                    int views = (video.getStat() != null) ? video.getStat().getView() : 0;
                    // 视频时长：duration 字段（秒）
                    int duration = video.getDuration();

                    System.out.println("Title: " + title);
                    System.out.println("Tags: " + videoTags);
                    System.out.println("URL: " + videoUrl);
                    System.out.println("Views: " + views);
                    System.out.println("Duration: " + duration + " seconds");
                    System.out.println("-----------------------------------");

                    // 存储到数据库
                    saveVideoToDatabase(title, videoTags, videoUrl, views, duration);
                }

                // 如果本页返回视频数量小于 ps，则认为已到最后一页
                if (videos.size() < ps) {
                    System.out.println("Page " + pn + " returned less than " + ps + " videos. Ending pagination.");
                    break;
                }
                pn++; // 下一页
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void saveVideoToDatabase(String title, String videoTags, String videoUrl, int views, int duration) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "INSERT INTO videos (video_title, video_tags, video_url, video_views, video_duration) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title);
            statement.setString(2, videoTags);
            statement.setString(3, videoUrl);
            statement.setInt(4, views);
            statement.setInt(5, duration);
            statement.executeUpdate();
            System.out.println("Video saved: " + title);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to save video: " + title);
        }
    }
}
