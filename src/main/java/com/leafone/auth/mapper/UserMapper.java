package com.leafone.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leafone.auth.model.User;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据 openid 查询已软删除的用户（绕过 @TableLogic 自动过滤）
     */
    @Select("SELECT * FROM users WHERE openid = #{openid} AND deleted_at IS NOT NULL LIMIT 1")
    User selectDeletedByOpenid(String openid);
}
