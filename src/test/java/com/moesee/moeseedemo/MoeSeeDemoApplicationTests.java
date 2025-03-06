package com.moesee.moeseedemo;

import com.moesee.moeseedemo.service.VideoScraperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootTest
@EnableScheduling
public class MoeSeeDemoApplicationTests {

    @Autowired
    private VideoScraperService videoScraperService;

    @Test
    void testScrapeVideos() throws InterruptedException {
        // 启动定时任务
        videoScraperService.scrapeVideos();

        // 模拟长时间运行
        Thread.sleep(3600000); // 让测试运行1小时（3600000毫秒）
    }
}
