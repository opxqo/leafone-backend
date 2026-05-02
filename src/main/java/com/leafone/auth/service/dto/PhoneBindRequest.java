package com.leafone.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "微信授权手机号绑定请求")
public class PhoneBindRequest {
    @NotBlank
    @Schema(description = "wx.getPhoneNumber() 返回的 code")
    private String code;
}
