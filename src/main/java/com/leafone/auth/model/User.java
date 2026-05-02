package com.leafone.auth.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
@Schema(description = "用户")
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "微信openid")
    private String openid;
    @Schema(description = "微信unionid")
    private String unionid;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "学号")
    private String studentNo;
    @Schema(description = "宿舍")
    private String dorm;
    @Schema(description = "密码哈希")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "头像URL")
    private String avatarUrl;
    @Schema(description = "性别（0=未知, 1=男, 2=女）")
    private Integer gender;
    @Schema(description = "角色（STUDENT/ADMIN）", example = "STUDENT")
    private String role;
    @Schema(description = "状态（1=正常, 2=封禁）")
    private Integer status;
    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private LocalDateTime deletedAt;
}
