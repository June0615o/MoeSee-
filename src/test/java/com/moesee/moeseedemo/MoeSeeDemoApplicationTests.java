package com.moesee.moeseedemo;

import com.moesee.moeseedemo.controller.RecommendUserController;
import com.moesee.moeseedemo.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MoeSeeDemoApplicationTests {

    @Autowired
    MockUserWatchingService mockUserWatchingService;

    @Test
    public void contextLoads() {
        mockUserWatchingService.mockUserWatching();
    }
}
