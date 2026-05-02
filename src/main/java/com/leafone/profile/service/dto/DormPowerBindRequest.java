package com.leafone.profile.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "绑定寝室房间号请求")
public class DormPowerBindRequest {

    @NotBlank(message = "房间号不能为空")
    @Size(max = 64, message = "房间号过长")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9\\-]+$", message = "房间号格式不正确（如：南1-103）")
    @Schema(description = "寝室房间号", example = "南1-103")
    private String roomName;

    @Size(max = 128, message = "密码过长")
    @Schema(description = "电费系统密码（不填则使用默认密码 cy@123）", example = "cy@123")
    private String password;
}
