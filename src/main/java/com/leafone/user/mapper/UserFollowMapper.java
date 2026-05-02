package com.leafone.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leafone.user.model.UserFollow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface UserFollowMapper extends BaseMapper<UserFollow> {

    @Select("SELECT " +
            "SUM(CASE WHEN following_id = #{userId} THEN 1 ELSE 0 END) AS followerCount, " +
            "SUM(CASE WHEN follower_id = #{userId} THEN 1 ELSE 0 END) AS followingCount " +
            "FROM user_follows " +
            "WHERE (following_id = #{userId} OR follower_id = #{userId}) AND deleted_at IS NULL")
    Map<String, Object> selectFollowCounts(@Param("userId") Long userId);
}
