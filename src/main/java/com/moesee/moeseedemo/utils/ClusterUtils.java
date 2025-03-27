package com.moesee.moeseedemo.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
   工具类 ClusterUtils 说明:
   它的作用是解析用户聚类ID,
   将逗号分隔的ID列表转换为整数列表.
   在获得其返回值列表之后,
   可以使用for的加强循环来遍历或获得对应的聚类id.
*/

@Component
public class ClusterUtils {
    public static List<Integer> parseClusterIds(String userClusterId) {
        if (userClusterId == null || userClusterId.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(userClusterId.split(","))
                .map(id -> Integer.valueOf(id.trim())) // 转换为 Integer
                .collect(Collectors.toList()); // 收集为列表
    }

    public static String clusterIds2Name(int clusterId) {
        switch (clusterId) {
            case 0:
                return "综艺";
            case 1:
                return "动画";
            case 2:
                return "鬼畜";
            case 3:
                return "舞蹈";
            case 4:
                return "娱乐";
            case 5:
                return "科技数码";
            case 6:
                return "美食";
            case 7:
                return "汽车";
            case 8:
                return "体育运动";
            case 9:
                return "电影";
            case 10:
                return "电视剧";
            case 11:
                return "纪录片";
            case 12:
                return "游戏";
            case 13:
                return "音乐";
            case 14:
                return "影视";
            case 15:
                return "知识";
            case 16:
                return "资讯";
            case 17:
                return "小剧场";
            case 18:
                return "时尚美妆";
            case 19:
                return "动物";
            case 20:
                return "vlog";
            case 21:
                return "绘画";
            case 22:
                return "人工智能";
            case 23:
                return "家装房产";
            case 24:
                return "户外";
            case 25:
                return "健身";
            case 26:
                return "手工";
            case 27:
                return "旅游出行";
            case 28:
                return "三农";
            case 29:
                return "亲子";
            case 30:
                return "健康";
            case 31:
                return "情感";
            case 32:
                return "生活经验";
            case 33:
                return "生活兴趣";
            case 34:
                return "公益";
            default:
                return "未知领域";
        }
    }
}
