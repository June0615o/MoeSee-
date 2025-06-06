package com.moesee.moeseedemo.mapper;

import com.moesee.moeseedemo.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 根据用户ID获取用户聚类ID
     * @param userId 用户ID
     * @return 用户聚类ID
     */

    @Select("SELECT user_cluster_id FROM users WHERE user_id = #{userId}")
    String getUserClusterIdById(int userId);

    @Select("SELECT user_id FROM users WHERE user_uid= #{userUid}")
    int getUserIdByUid(int userUid);
    @Select("SELECT user_uid FROM users WHERE user_id = #{userId}")
    int getUserUidById(int userId);
    @Select("SELECT user_cluster_id FROM users WHERE user_uid = #{userUid}")
    String getUserClusterIdByUid(int userUid);
    @Select("SELECT user_id FROM user_account WHERE user_account = #{phone}")
    Integer getUserIdByPhone(@Param("phone") String phone);

    /**
     * 根据聚类ID查找相似用户
     * @param clusterId 当前用户聚类ID
     * @param excludeUserId 当前用户ID（需排除自己）
     * @param limit 返回用户数量限制
     * @return 相似用户列表
     */
    @Select("SELECT * FROM users WHERE FIND_IN_SET(#{clusterId}, user_cluster_id) AND user_id != #{excludeUserId} ORDER BY RAND() LIMIT #{limit}")
    List<User> findUsersByClusterId(@Param("clusterId") Integer clusterId,
                                    @Param("excludeUserId") Integer excludeUserId,
                                    @Param("limit") Integer limit);

    int getMaxUserUid();

    void insertUserAccount(@Param("userAccount")String phone,
                           @Param("userPassword")String password);

    void addUserId(@Param("userId")int userId,
                   @Param("phone")String phone);

    List<Integer> getUsersByExactClusterIds(@Param("clusterIds")List<Integer> clusterIds,
                                            @Param("excludeUserId")Integer excludeUserId);

    List<Integer> getUsersByClusterId(@Param("clusterId")Integer clusterId,
                                      @Param("excludeUserId")Integer excludeUserId);

    void registerUser(@Param("userUid")Integer userUid,
                     @Param("userPreferredTags")String userPreferredTags,
                     @Param("userClusterIds")String userClusterIds);

    int validateUser(@Param("userAccount")String userAccount,
                     @Param("password")String userPassword);
}

