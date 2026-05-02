package com.leafone.auth.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenRequest {
    @NotBlank
    @Schema(description = "刷新令牌")
    private String refreshToken;
}
