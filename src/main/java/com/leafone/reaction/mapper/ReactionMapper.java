package com.leafone.reaction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leafone.reaction.model.Reaction;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDateTime;

public interface ReactionMapper extends BaseMapper<Reaction> {

    @Update("UPDATE reactions SET deleted_at = #{now} WHERE id = #{id} AND deleted_at IS NULL")
    int softDelete(@Param("id") Long id, @Param("now") LocalDateTime now);

    @Update("UPDATE reactions SET deleted_at = #{now} WHERE user_id = #{userId} AND target_type = #{targetType} " +
            "AND target_id = #{targetId} AND reaction_type = #{reactionType} AND deleted_at IS NULL")
    int softDeleteByTarget(@Param("userId") Long userId, @Param("targetType") String targetType,
                           @Param("targetId") Long targetId, @Param("reactionType") String reactionType,
                           @Param("now") LocalDateTime now);
}
