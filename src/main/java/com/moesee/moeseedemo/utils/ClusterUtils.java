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
}
