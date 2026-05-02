package com.leafone.profile.service.dto;

import com.leafone.auth.model.User;
import com.leafone.user.model.StudentProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人资料响应")
public class ProfileResponse {
    @Schema(description = "用户基本信息")
    private User user;
    @Schema(description = "学生档案信息")
    private StudentProfile studentProfile;
}
