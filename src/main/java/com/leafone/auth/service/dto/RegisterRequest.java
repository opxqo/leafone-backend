package com.leafone.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "注册请求")
public class RegisterRequest {
    @NotBlank
    @Schema(description = "真实姓名", example = "张三")
    private String realName;
    @NotBlank
    @Schema(description = "学号", example = "2024001")
    private String studentNo;
    @NotBlank
    @Schema(description = "学院", example = "计算机学院")
    private String college;
    @Schema(description = "专业", example = "软件工程")
    private String major;
    @Schema(description = "年级", example = "2024级")
    private String grade;
    @Schema(description = "手机号", example = "13800138000")
    private String phone;
    @NotBlank
    @Schema(description = "密码", example = "123456")
    private String password;
}
