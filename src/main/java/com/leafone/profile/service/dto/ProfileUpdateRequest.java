package com.leafone.profile.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "修改个人信息请求")
public class ProfileUpdateRequest {
    @Size(max = 64, message = "昵称不能超过64字")
    @Schema(description = "昵称", example = "林一航")
    private String nickname;

    @Size(max = 512, message = "头像URL过长")
    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "性别：0=未知 1=男 2=女", example = "1")
    private Integer gender;

    @Size(max = 64, message = "宿舍信息过长")
    @Schema(description = "宿舍", example = "梅园1号楼302")
    private String dorm;
}
