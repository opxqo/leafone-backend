package com.leafone.comment.controller;

import com.leafone.comment.model.PostComment;
import com.leafone.comment.service.CommentService;
import com.leafone.comment.service.dto.CommentCreateRequest;
import com.leafone.common.response.PageResult;
import com.leafone.common.util.AuthUtil;
import com.leafone.common.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论管理", description = "帖子评论的查看、发表和删除")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "评论列表", description = "获取帖子的评论列表，支持按最新或最热排序")
    @GetMapping("/posts/{postId}/comments")
    public R<PageResult<PostComment>> commentList(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Parameter(description = "排序方式（latest=最新, hot=最热）") @RequestParam(required = false, defaultValue = "latest") String sort,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        pageSize = PageResult.capPageSize(pageSize);
        return R.ok(commentService.commentList(postId, sort, page, pageSize));
    }

    @Operation(summary = "发表评论", description = "对帖子发表评论，支持回复其他评论，需登录")
    @PostMapping("/posts/{postId}/comments")
    public R<PostComment> createComment(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(commentService.createComment(postId, request, userId));
    }

    @Operation(summary = "删除评论", description = "删除自己的评论，需登录")
    @DeleteMapping("/comments/{commentId}")
    public R<Void> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        commentService.deleteComment(commentId, userId);
        return R.ok();
    }
}
