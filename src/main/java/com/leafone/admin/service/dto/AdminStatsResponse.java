package com.leafone.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "后台数据统计响应")
public class AdminStatsResponse {
    @Schema(description = "总用户数", example = "1024")
    private long totalUsers;
    @Schema(description = "今日新增用户", example = "12")
    private long todayNewUsers;
    @Schema(description = "总帖子数", example = "5000")
    private long totalPosts;
    @Schema(description = "今日新增帖子", example = "56")
    private long todayNewPosts;
    @Schema(description = "总评论数", example = "20000")
    private long totalComments;
    @Schema(description = "今日新增评论", example = "120")
    private long todayNewComments;
    @Schema(description = "待审核帖子数", example = "3")
    private long pendingPosts;
    @Schema(description = "待处理反馈数", example = "5")
    private long pendingFeedbacks;
    @Schema(description = "被封禁用户数", example = "2")
    private long bannedUsers;
}
