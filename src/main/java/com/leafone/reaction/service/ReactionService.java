package com.leafone.reaction.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leafone.common.exception.BizException;
import com.leafone.post.mapper.PostMapper;
import com.leafone.reaction.mapper.ReactionMapper;
import com.leafone.reaction.model.Reaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionMapper reactionMapper;
    private final PostMapper postMapper;

    @Transactional(rollbackFor = Exception.class)
    public void likePost(Long postId, Long userId) {
        checkDuplicate(userId, "POST", postId, "LIKE");
        insertReaction(userId, "POST", postId, "LIKE");
        postMapper.incrementLikeCount(postId);
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
    }

    @Transactional(rollbackFor = Exception.class)
    public void unfavoritePost(Long postId, Long userId) {
        int affected = reactionMapper.softDeleteByTarget(userId, "POST", postId, "FAVORITE", LocalDateTime.now());
        if (affected == 0) throw new BizException(40400, "No favorite found");
    }

    @Transactional(rollbackFor = Exception.class)
    public void likeComment(Long commentId, Long userId) {
        checkDuplicate(userId, "COMMENT", commentId, "LIKE");
        insertReaction(userId, "COMMENT", commentId, "LIKE");
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlikeComment(Long commentId, Long userId) {
        int affected = reactionMapper.softDeleteByTarget(userId, "COMMENT", commentId, "LIKE", LocalDateTime.now());
        if (affected == 0) throw new BizException(40400, "No like found");
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
