package com.leafone.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leafone.post.model.Post;
import org.apache.ibatis.annotations.Update;

public interface PostMapper extends BaseMapper<Post> {

    @Update("UPDATE posts SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(Long id);

    @Update("UPDATE posts SET like_count = GREATEST(like_count - 1, 0) WHERE id = #{id}")
    int decrementLikeCount(Long id);

    @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(Long id);

    @Update("UPDATE posts SET share_count = share_count + 1 WHERE id = #{id}")
    int incrementShareCount(Long id);

    @Update("UPDATE posts SET comment_count = comment_count + 1 WHERE id = #{id}")
    int incrementCommentCount(Long id);

    @Update("UPDATE posts SET comment_count = GREATEST(comment_count - 1, 0) WHERE id = #{id}")
    int decrementCommentCount(Long id);
}
