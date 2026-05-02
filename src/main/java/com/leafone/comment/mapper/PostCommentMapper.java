package com.leafone.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leafone.comment.model.PostComment;
import org.apache.ibatis.annotations.Update;

public interface PostCommentMapper extends BaseMapper<PostComment> {

    @Update("UPDATE post_comments SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE post_comments SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    int decrementLikeCount(Long id);
}
