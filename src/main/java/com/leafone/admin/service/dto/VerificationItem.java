package com.leafone.admin.service.dto;

import com.leafone.auth.model.User;
import com.leafone.user.model.StudentProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "学生认证审核项")
public class VerificationItem {

    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "头像URL")
    private String avatarUrl;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "学号")
    private String studentNo;

    // StudentProfile fields
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "学部")
    private String college;
    @Schema(description = "专业")
    private String major;
    @Schema(description = "年级")
    private String grade;
    @Schema(description = "认证状态（0=待审核, 1=已通过, 2=已拒绝）")
    private Integer verified;
    @Schema(description = "认证提交时间")
    private LocalDateTime createdAt;

    public static VerificationItem from(User user, StudentProfile profile) {
        VerificationItem item = new VerificationItem();
        item.setUserId(user.getId());
        item.setNickname(user.getNickname());
        item.setAvatarUrl(user.getAvatarUrl());
        item.setPhone(user.getPhone());
        item.setStudentNo(user.getStudentNo());
        if (profile != null) {
            item.setRealName(profile.getRealName());
            item.setCollege(profile.getCollege());
            item.setMajor(profile.getMajor());
            item.setGrade(profile.getGrade());
            item.setVerified(profile.getVerified());
            item.setCreatedAt(profile.getCreatedAt());
        }
        return item;
    }
}
