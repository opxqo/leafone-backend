package com.leafone.admin.controller;

import com.leafone.admin.service.AdminService;
import com.leafone.admin.service.dto.*;
import com.leafone.auth.model.User;
import com.leafone.comment.model.PostComment;
import com.leafone.common.response.PageResult;
import com.leafone.common.response.R;
import com.leafone.module.model.Module;
import com.leafone.post.model.HomeHeadline;
import com.leafone.post.model.Post;
import com.leafone.post.model.Topic;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "后台管理", description = "管理员专用接口，帖子审核、用户管理、版块/话题/头条管理、通知管理、数据统计等")
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ==================== 帖子管理 ====================

    @Operation(summary = "帖子审核列表", description = "管理员查看帖子列表，可按状态筛选")
    @GetMapping("/posts")
    public R<PageResult<Post>> adminPosts(
            @Parameter(description = "帖子状态（1=正常, 2=隐藏, 3=删除）") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(adminService.adminPosts(status, page, pageSize));
    }

    @Operation(summary = "修改帖子状态", description = "管理员审核帖子，修改帖子状态")
    @PutMapping("/posts/{postId}/status")
    public R<Void> updatePostStatus(
            @Parameter(description = "帖子ID") @PathVariable Long postId,
            @Parameter(description = "目标状态（1=正常, 2=隐藏, 3=删除）") @RequestParam Integer status) {
        adminService.updatePostStatus(postId, status);
        return R.ok();
    }

    // ==================== 用户管理 ====================

    @Operation(summary = "用户列表", description = "管理员查看用户列表，支持搜索和角色筛选")
    @GetMapping("/users")
    public R<PageResult<User>> adminUsers(
            @Parameter(description = "搜索关键词（昵称/学号/手机号）") @RequestParam(required = false) String keyword,
            @Parameter(description = "角色筛选（USER/ADMIN）") @RequestParam(required = false) String role,
            @Parameter(description = "状态筛选（1=正常, 2=封禁）") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(adminService.adminUsers(keyword, role, status, page, pageSize));
    }

    @Operation(summary = "用户详情", description = "查看指定用户的详细信息")
    @GetMapping("/users/{userId}")
    public R<User> userDetail(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return R.ok(adminService.userDetail(userId));
    }

    @Operation(summary = "解封用户", description = "解除用户封禁状态")
    @PutMapping("/users/{userId}/unban")
    public R<Void> unbanUser(@Parameter(description = "用户ID") @PathVariable Long userId) {
        adminService.unbanUser(userId);
        return R.ok();
    }

    @Operation(summary = "修改用户角色", description = "修改用户角色（USER/ADMIN）")
    @PutMapping("/users/{userId}/role")
    public R<Void> updateUserRole(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "目标角色（USER/ADMIN）") @RequestParam String role) {
        adminService.updateUserRole(userId, role);
        return R.ok();
    }

    @Operation(summary = "审核学生认证", description = "通过或拒绝学生认证申请")
    @PutMapping("/users/{userId}/verify-student")
    public R<Void> verifyStudent(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "是否通过（true=通过, false=拒绝）") @RequestParam boolean approved) {
        adminService.verifyStudent(userId, approved);
        return R.ok();
    }

    // ==================== 版块管理 ====================

    @Operation(summary = "创建版块", description = "管理员创建新论坛版块")
    @PostMapping("/modules")
    public R<Module> createModule(@Valid @RequestBody ModuleCreateRequest request) {
        return R.ok(adminService.createModule(request));
    }

    @Operation(summary = "编辑版块", description = "管理员编辑版块信息")
    @PutMapping("/modules/{moduleId}")
    public R<Void> updateModule(
            @Parameter(description = "版块ID") @PathVariable Long moduleId,
            @Valid @RequestBody ModuleCreateRequest request) {
        adminService.updateModule(moduleId, request);
        return R.ok();
    }

    @Operation(summary = "删除版块", description = "管理员删除版块")
    @DeleteMapping("/modules/{moduleId}")
    public R<Void> deleteModule(@Parameter(description = "版块ID") @PathVariable Long moduleId) {
        adminService.deleteModule(moduleId);
        return R.ok();
    }

    // ==================== 话题管理 ====================

    @Operation(summary = "创建话题", description = "管理员创建新话题")
    @PostMapping("/topics")
    public R<Topic> createTopic(@Valid @RequestBody TopicCreateRequest request) {
        return R.ok(adminService.createTopic(request));
    }

    @Operation(summary = "编辑话题", description = "管理员编辑话题信息")
    @PutMapping("/topics/{topicId}")
    public R<Void> updateTopic(
            @Parameter(description = "话题ID") @PathVariable Long topicId,
            @Valid @RequestBody TopicCreateRequest request) {
        adminService.updateTopic(topicId, request);
        return R.ok();
    }

    @Operation(summary = "删除话题", description = "管理员删除话题")
    @DeleteMapping("/topics/{topicId}")
    public R<Void> deleteTopic(@Parameter(description = "话题ID") @PathVariable Long topicId) {
        adminService.deleteTopic(topicId);
        return R.ok();
    }

    // ==================== 头条管理 ====================

    @Operation(summary = "头条列表", description = "管理员查看所有头条")
    @GetMapping("/headlines")
    public R<PageResult<HomeHeadline>> adminHeadlines(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(adminService.adminHeadlines(page, pageSize));
    }

    @Operation(summary = "创建头条", description = "管理员创建首页头条")
    @PostMapping("/headlines")
    public R<HomeHeadline> createHeadline(@Valid @RequestBody HeadlineCreateRequest request) {
        return R.ok(adminService.createHeadline(request));
    }

    @Operation(summary = "编辑头条", description = "管理员编辑头条信息")
    @PutMapping("/headlines/{headlineId}")
    public R<Void> updateHeadline(
            @Parameter(description = "头条ID") @PathVariable Long headlineId,
            @Valid @RequestBody HeadlineCreateRequest request) {
        adminService.updateHeadline(headlineId, request);
        return R.ok();
    }

    @Operation(summary = "删除头条", description = "管理员删除头条")
    @DeleteMapping("/headlines/{headlineId}")
    public R<Void> deleteHeadline(@Parameter(description = "头条ID") @PathVariable Long headlineId) {
        adminService.deleteHeadline(headlineId);
        return R.ok();
    }

    // ==================== 评论管理 ====================

    @Operation(summary = "评论列表", description = "管理员查看评论列表，支持按帖子/用户筛选")
    @GetMapping("/comments")
    public R<PageResult<PostComment>> adminComments(
            @Parameter(description = "帖子ID") @RequestParam(required = false) Long postId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize) {
        return R.ok(adminService.adminComments(postId, userId, page, pageSize));
    }

    @Operation(summary = "管理员删除评论", description = "管理员强制删除评论")
    @DeleteMapping("/comments/{commentId}")
    public R<Void> deleteComment(@Parameter(description = "评论ID") @PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        return R.ok();
    }

    // ==================== 反馈处理 ====================

    @Operation(summary = "修改反馈状态", description = "管理员处理反馈，修改反馈状态")
    @PutMapping("/feedbacks/{feedbackId}/status")
    public R<Void> updateFeedbackStatus(
            @Parameter(description = "反馈ID") @PathVariable Long feedbackId,
            @Parameter(description = "目标状态（PENDING/RESOLVED/REJECTED）") @RequestParam String status) {
        adminService.updateFeedbackStatus(feedbackId, status);
        return R.ok();
    }

    // ==================== 数据统计 ====================

    @Operation(summary = "数据统计", description = "获取后台统计数据（用户/帖子/评论/反馈等）")
    @GetMapping("/stats")
    public R<AdminStatsResponse> stats() {
        return R.ok(adminService.stats());
    }

    // ==================== 通知管理 ====================

    @Operation(summary = "发送系统通知", description = "管理员发送系统通知，支持全体用户或指定用户列表")
    @PostMapping("/notifications")
    public R<Void> sendNotification(@Valid @RequestBody NotificationSendRequest request) {
        adminService.sendNotification(request);
        return R.ok();
    }
}
