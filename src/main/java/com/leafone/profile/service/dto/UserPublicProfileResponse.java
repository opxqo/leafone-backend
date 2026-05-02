package com.leafone.profile.service.dto;

import com.leafone.user.model.StudentProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户公开资料响应")
public class UserPublicProfileResponse {
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "头像URL")
    private String avatarUrl;
    @Schema(description = "性别（0=未知, 1=男, 2=女）")
    private Integer gender;
    @Schema(description = "学生档案（仅已认证用户可见）")
    private StudentProfile studentProfile;
    @Schema(description = "帖子数")
    private Long postCount;
    @Schema(description = "粉丝数")
    private Long followerCount;
    @Schema(description = "关注数")
    private Long followingCount;
    @Schema(description = "当前用户是否已关注")
    private Boolean followed;
}
