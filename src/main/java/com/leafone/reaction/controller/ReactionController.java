package com.leafone.reaction.controller;

import com.leafone.common.response.R;
import com.leafone.common.util.AuthUtil;
import com.leafone.reaction.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "互动操作", description = "帖子和评论的点赞、收藏等互动接口")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @Operation(summary = "点赞帖子", description = "对帖子点赞，需登录")
    @PostMapping("/posts/{postId}/like")
    public R<Void> likePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        reactionService.likePost(postId, userId);
        return R.ok();
    }

    @Operation(summary = "取消点赞帖子", description = "取消对帖子的点赞，需登录")
    @DeleteMapping("/posts/{postId}/like")
    public R<Void> unlikePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        reactionService.unlikePost(postId, userId);
        return R.ok();
    }

    @Operation(summary = "收藏帖子", description = "收藏帖子，需登录")
    @PostMapping("/posts/{postId}/favorite")
    public R<Void> favoritePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        reactionService.favoritePost(postId, userId);
        return R.ok();
    }

    @Operation(summary = "取消收藏帖子", description = "取消对帖子的收藏，需登录")
    @DeleteMapping("/posts/{postId}/favorite")
    public R<Void> unfavoritePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        reactionService.unfavoritePost(postId, userId);
        return R.ok();
    }

    @Operation(summary = "点赞评论", description = "对评论点赞，需登录")
    @PostMapping("/comments/{commentId}/like")
    public R<Void> likeComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        reactionService.likeComment(commentId, userId);
        return R.ok();
    }

    @Operation(summary = "取消点赞评论", description = "取消对评论的点赞，需登录")
    @DeleteMapping("/comments/{commentId}/like")
    public R<Void> unlikeComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        reactionService.unlikeComment(commentId, userId);
        return R.ok();
    }
}
