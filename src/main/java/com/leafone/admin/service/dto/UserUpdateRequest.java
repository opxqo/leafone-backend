package com.leafone.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "管理员编辑用户请求")
public class UserUpdateRequest {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "宿舍")
    private String dorm;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "性别（0=未知, 1=男, 2=女）")
    private Integer gender;
}
