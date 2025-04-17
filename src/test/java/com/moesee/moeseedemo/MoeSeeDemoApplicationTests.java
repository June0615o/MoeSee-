package com.moesee.moeseedemo;

import com.moesee.moeseedemo.controller.RecommendUserController;
import com.moesee.moeseedemo.service.*;
import com.moesee.moeseedemo.utils.RedisIdWorker;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class MoeSeeDemoApplicationTests {

    @Autowired
    MockUserWatchingService mockUserWatchingService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Autowired
    private SeckillService seckillService;

    @Test
    public void contextLoads() {
        for( int i=0; i<2000; i++){
            mockUserWatchingService.mockUserWatching();
        }
    }
}
