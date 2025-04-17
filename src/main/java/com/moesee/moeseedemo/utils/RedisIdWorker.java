package com.moesee.moeseedemo.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final long BEGIN_TIMESTAMP = 1735689600L;

    public long nextId(String keyPrefix){
        //前缀区分不同的业务
        /*
        生成策略：时间戳与随机序列号
         */
        LocalDateTime now = LocalDateTime.now();
        long nowSecond=now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond-BEGIN_TIMESTAMP;
        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Long count = stringRedisTemplate.opsForValue().increment("icr:"+keyPrefix+":"+date);
        return timestamp <<  32 | count;
    }
    public static void main(String[] args) {
        LocalDateTime time = LocalDateTime.of(2025,1,1,0,0,0);
        long second = time.toEpochSecond(ZoneOffset.UTC);
        System.out.println("second:"+second);
    }
}
