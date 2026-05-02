package com.leafone.profile.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "提交反馈请求")
public class FeedbackCreateRequest {
    @NotBlank
    @Schema(description = "反馈内容")
    private String content;

    @Schema(description = "联系方式（手机号/邮箱）")
    private String contact;
}
