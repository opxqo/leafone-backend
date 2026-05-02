package com.leafone.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leafone.message.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MessageMapper extends BaseMapper<Message> {

    @Insert("<script>" +
            "INSERT INTO messages (id, user_id, sender_id, type, title, content, target_type, target_id, created_at) VALUES " +
            "<foreach collection='messages' item='msg' separator=','>" +
            "(#{msg.id}, #{msg.userId}, #{msg.senderId}, #{msg.type}, #{msg.title}, #{msg.content}, #{msg.targetType}, #{msg.targetId}, NOW())" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("messages") List<Message> messages);
}
