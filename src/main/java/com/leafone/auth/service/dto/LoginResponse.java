package com.leafone.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录响应")
public class LoginResponse {
    @Schema(description = "访问令牌")
    private String accessToken;
    @Schema(description = "刷新令牌")
    private String refreshToken;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "用户角色", example = "USER")
    private String role;
    @Schema(description = "是否已完成学生认证")
    private boolean studentVerified;
    @Schema(description = "是否为新注册用户")
    private boolean newUser;
}
