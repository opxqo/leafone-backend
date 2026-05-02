package com.leafone.profile.controller;

import com.leafone.common.response.PageResult;
import com.leafone.common.util.AuthUtil;
import com.leafone.common.response.R;
import com.leafone.post.model.Post;
import com.leafone.profile.service.ProfileService;
import com.leafone.profile.service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "个人中心", description = "用户个人信息、收藏和发布的帖子")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "个人信息", description = "获取当前登录用户的个人资料，需登录")
    @GetMapping("/me/profile")
    public R<ProfileResponse> myProfile(@AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(profileService.myProfile(userId));
    }

    @Operation(summary = "修改个人信息", description = "修改当前登录用户的昵称、头像、性别、宿舍，需登录")
    @PutMapping("/me/profile")
    public R<Void> updateProfile(@AuthenticationPrincipal Long userId,
                                 @Valid @RequestBody ProfileUpdateRequest request) {
        AuthUtil.requireLogin(userId);
        profileService.updateProfile(userId, request);
        return R.ok();
    }

    @Operation(summary = "我的收藏", description = "获取当前用户收藏的帖子列表，需登录")
    @GetMapping("/me/favorites")
    public R<PageResult<Post>> myFavorites(@AuthenticationPrincipal Long userId,
                                           @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
                                           @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        AuthUtil.requireLogin(userId);
        pageSize = PageResult.capPageSize(pageSize);
        return R.ok(profileService.myFavorites(userId, page, pageSize));
    }

    @Operation(summary = "我的帖子", description = "获取当前用户发布的帖子列表，需登录")
    @GetMapping("/me/posts")
    public R<PageResult<Post>> myPosts(@AuthenticationPrincipal Long userId,
                                       @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
                                       @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        AuthUtil.requireLogin(userId);
        pageSize = PageResult.capPageSize(pageSize);
        return R.ok(profileService.myPosts(userId, page, pageSize));
    }

    @Operation(summary = "查看他人主页", description = "查看指定用户的公开资料、帖子数、粉丝数等")
    @GetMapping("/users/{userId}/profile")
    public R<UserPublicProfileResponse> userProfile(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal Long currentUserId) {
        return R.ok(profileService.userProfile(userId, currentUserId));
    }

    @Operation(summary = "关注用户", description = "关注指定用户，需登录")
    @PostMapping("/users/{userId}/follow")
    public R<Void> follow(
            @Parameter(description = "目标用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal Long currentUserId) {
        AuthUtil.requireLogin(currentUserId);
        profileService.follow(currentUserId, userId);
        return R.ok();
    }

    @Operation(summary = "取消关注", description = "取消关注指定用户，需登录")
    @DeleteMapping("/users/{userId}/follow")
    public R<Void> unfollow(
            @Parameter(description = "目标用户ID") @PathVariable Long userId,
            @AuthenticationPrincipal Long currentUserId) {
        AuthUtil.requireLogin(currentUserId);
        profileService.unfollow(currentUserId, userId);
        return R.ok();
    }

    @Operation(summary = "提交反馈", description = "提交用户反馈，需登录")
    @PostMapping("/feedbacks")
    public R<Void> submitFeedback(
            @Valid @RequestBody FeedbackCreateRequest request,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        profileService.submitFeedback(userId, request);
        return R.ok();
    }
}
