package com.moesee.moeseedemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 允许所有路径
                        .allowedOrigins("http://localhost:5173") // 允许前端的 Origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法类型
                        .allowedHeaders("Authorization", "Content-Type") // 显式允许 Authorization 头
                        .allowCredentials(true); // 是否允许携带认证信息
            }
        };
    }
}