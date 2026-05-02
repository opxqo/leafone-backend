package com.leafone.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "微信登录请求")
public class WechatLoginRequest {
    @NotBlank(message = "code不能为空")
    @Schema(description = "wx.login() 返回的临时登录凭证 code")
    private String code;
}
