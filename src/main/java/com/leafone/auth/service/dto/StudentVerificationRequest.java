package com.leafone.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "学生认证请求")
public class StudentVerificationRequest {
    @NotBlank
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @NotBlank
    @Schema(description = "学号", example = "2024001")
    private String studentNo;

    @NotBlank
    @Schema(description = "学部", example = "计算机学部")
    private String college;

    @Schema(description = "专业", example = "软件工程")
    private String major;

    @Schema(description = "入学年份", example = "2024")
    private String grade;
}
