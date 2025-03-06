package com.moesee.moeseedemo;

import com.moesee.moeseedemo.service.VideoScraperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MoeSeeDemoApplicationTests {

    @Autowired
    private VideoScraperService videoScraperService;

    @Test
    public void testScrapeVideos() {
        videoScraperService.scrapeVideos(); // 调用实例方法
    }
}
