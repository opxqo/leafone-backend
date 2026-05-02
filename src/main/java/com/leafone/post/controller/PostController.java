package com.leafone.post.controller;

import com.leafone.common.response.PageResult;
import com.leafone.common.util.AuthUtil;
import com.leafone.common.response.R;
import com.leafone.post.model.Post;
import com.leafone.post.service.PostService;
import com.leafone.post.service.dto.HomeResponse;
import com.leafone.post.service.dto.PostCreateRequest;
import com.leafone.post.service.dto.PostDetailResponse;
import com.leafone.post.service.dto.PostUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "帖子管理", description = "帖子的增删改查、首页内容、分享等接口")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "首页内容", description = "获取首页轮播头条和最新帖子列表")
    @GetMapping("/home")
    public R<HomeResponse> home() {
        return R.ok(postService.home());
    }

    @Operation(summary = "帖子列表", description = "分页获取帖子列表，支持按版块、标签、关键词筛选，支持瀑布流模式（latest/recommend/following）")
    @GetMapping("/posts")
    public R<PageResult<Post>> postList(
            @Parameter(description = "版块ID") @RequestParam(required = false) Long moduleId,
            @Parameter(description = "标签筛选（featured=精选）") @RequestParam(required = false) String tab,
            @Parameter(description = "瀑布流模式：latest=最新(默认), recommend=推荐, following=关注") @RequestParam(required = false, defaultValue = "latest") String feed,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Long userId) {
        pageSize = PageResult.capPageSize(pageSize);
        return R.ok(postService.postList(moduleId, tab, feed, keyword, page, pageSize, userId));
    }

    @Operation(summary = "帖子详情", description = "获取帖子详细信息，包含当前用户的点赞/收藏状态")
    @GetMapping("/posts/{postId}")
    public R<PostDetailResponse> postDetail(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        return R.ok(postService.postDetail(postId, userId));
    }

    @Operation(summary = "发布帖子", description = "创建新帖子，需登录")
    @PostMapping("/posts")
    public R<Post> createPost(@Valid @RequestBody PostCreateRequest request,
                              @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(postService.createPost(request, userId));
    }

    @Operation(summary = "分享帖子", description = "增加帖子分享计数")
    @PostMapping("/posts/{postId}/share")
    public R<Void> sharePost(@Parameter(description = "帖子ID") @PathVariable Long postId) {
        postService.sharePost(postId);
        return R.ok();
    }

    @Operation(summary = "编辑帖子", description = "编辑自己发布的帖子，需登录")
    @PutMapping("/posts/{postId}")
    public R<Post> updatePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(postService.updatePost(postId, request, userId));
    }

    @Operation(summary = "删除帖子", description = "删除自己发布的帖子，需登录")
    @DeleteMapping("/posts/{postId}")
    public R<Void> deletePost(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        postService.deletePost(postId, userId);
        return R.ok();
    }
}
