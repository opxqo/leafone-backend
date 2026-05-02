package com.leafone.reaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leafone.comment.mapper.PostCommentMapper;
import com.leafone.comment.model.PostComment;
import com.leafone.common.exception.BizException;
import com.leafone.message.event.NotificationEvent;
import com.leafone.post.mapper.PostMapper;
import com.leafone.post.model.Post;
import com.leafone.reaction.mapper.ReactionMapper;
import com.leafone.reaction.model.Reaction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionMapper reactionMapper;
    private final PostMapper postMapper;
    private final PostCommentMapper commentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public void likePost(Long postId, Long userId) {
        checkDuplicate(userId, "POST", postId, "LIKE");
        insertReaction(userId, "POST", postId, "LIKE");
        postMapper.incrementLikeCount(postId);

        Post post = postMapper.selectById(postId);
        if (post != null && !post.getAuthorId().equals(userId)) {
            eventPublisher.publishEvent(new NotificationEvent(this,
                    post.getAuthorId(), userId, "LIKE",
                    "赞了你的帖子", "", "POST", postId));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlikePost(Long postId, Long userId) {
        int affected = reactionMapper.softDeleteByTarget(userId, "POST", postId, "LIKE", LocalDateTime.now());
        if (affected == 0) throw new BizException(40400, "No like found");
        postMapper.decrementLikeCount(postId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void favoritePost(Long postId, Long userId) {
        checkDuplicate(userId, "POST", postId, "FAVORITE");
        insertReaction(userId, "POST", postId, "FAVORITE");
        postMapper.incrementFavoriteCount(postId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unfavoritePost(Long postId, Long userId) {
        int affected = reactionMapper.softDeleteByTarget(userId, "POST", postId, "FAVORITE", LocalDateTime.now());
        if (affected == 0) throw new BizException(40400, "No favorite found");
        postMapper.decrementFavoriteCount(postId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId, Long userId) {
        checkDuplicate(userId, "COMMENT", commentId, "LIKE");
        insertReaction(userId, "COMMENT", commentId, "LIKE");
        commentMapper.incrementLikeCount(commentId);

        PostComment comment = commentMapper.selectById(commentId);
        if (comment != null && !comment.getUserId().equals(userId)) {
            Post post = postMapper.selectById(comment.getPostId());
            String postTitle = post != null ? post.getTitle() : "";
            eventPublisher.publishEvent(new NotificationEvent(this,
                    comment.getUserId(), userId, "COMMENT_LIKE",
                    "赞了你的评论", "在「" + postTitle + "」中赞了你的评论", "COMMENT", commentId));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlikeComment(Long commentId, Long userId) {
        int affected = reactionMapper.softDeleteByTarget(userId, "COMMENT", commentId, "LIKE", LocalDateTime.now());
        if (affected == 0) throw new BizException(40400, "No like found");
        commentMapper.decrementLikeCount(commentId);
    }

    private void checkDuplicate(Long userId, String targetType, Long targetId, String reactionType) {
        LambdaQueryWrapper<Reaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reaction::getUserId, userId)
               .eq(Reaction::getTargetType, targetType)
               .eq(Reaction::getTargetId, targetId)
               .eq(Reaction::getReactionType, reactionType)
               .isNull(Reaction::getDeletedAt);
        if (reactionMapper.selectCount(wrapper) > 0) {
            throw new BizException(40900, "Already " + reactionType.toLowerCase() + "d");
        }
    }

    private void insertReaction(Long userId, String targetType, Long targetId, String reactionType) {
        Reaction reaction = new Reaction();
        reaction.setUserId(userId);
        reaction.setTargetType(targetType);
        reaction.setTargetId(targetId);
        reaction.setReactionType(reactionType);
        reactionMapper.insert(reaction);
    }
}
