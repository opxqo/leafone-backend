package com.leafone.comment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafone.comment.mapper.PostCommentMapper;
import com.leafone.comment.model.PostComment;
import com.leafone.comment.service.dto.CommentCreateRequest;
import com.leafone.common.exception.BizException;
import com.leafone.common.response.PageResult;
import com.leafone.post.mapper.PostMapper;
import com.leafone.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostCommentMapper commentMapper;
    private final PostMapper postMapper;

    public PageResult<PostComment> commentList(Long postId, String sort, int page, int pageSize) {
        LambdaQueryWrapper<PostComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostComment::getPostId, postId)
               .eq(PostComment::getStatus, 1)
               .isNull(PostComment::getDeletedAt);

        if ("hot".equals(sort)) {
            wrapper.orderByDesc(PostComment::getLikeCount)
                   .orderByAsc(PostComment::getCreatedAt);
        } else {
            wrapper.orderByAsc(PostComment::getCreatedAt);
        }

        Page<PostComment> pageResult = commentMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(pageResult.getRecords(), page, pageSize, pageResult.getTotal());
    }

    @Transactional(rollbackFor = Exception.class)
    public PostComment createComment(Long postId, CommentCreateRequest request, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BizException(40400, "Post not found");

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setReplyToUserId(request.getReplyToUserId());
        comment.setLikeCount(0);
        comment.setStatus(1);
        commentMapper.insert(comment);

        postMapper.incrementCommentCount(postId);
        return comment;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = commentMapper.selectById(commentId);
        if (comment == null) throw new BizException(40400, "Comment not found");
        if (!comment.getUserId().equals(userId)) throw new BizException(40300, "只能删除自己的评论");

        comment.setStatus(3);
        commentMapper.updateById(comment);

        postMapper.decrementCommentCount(comment.getPostId());
    }
}
