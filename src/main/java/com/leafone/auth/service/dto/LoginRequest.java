package com.leafone.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginRequest {
    @Schema(description = "学号", example = "2024001")
    private String studentNo;
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    @NotBlank
    @Schema(description = "密码", example = "123456")
    private String password;
}
