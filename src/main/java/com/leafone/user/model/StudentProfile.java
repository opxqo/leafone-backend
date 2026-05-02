package com.leafone.user.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("student_profiles")
@Schema(description = "学生档案")
public class StudentProfile {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "档案ID")
    private Long id;
    @Schema(description = "关联用户ID")
    private Long userId;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "学号")
    private String studentNo;
    @Schema(description = "学部")
    private String college;
    @Schema(description = "专业")
    private String major;
    @Schema(description = "年级")
    private String grade;
    @Schema(description = "身份标签")
    private String identityLabel;
    @Schema(description = "寝室房间号（如 南1-103）")
    private String roomName;
    @Schema(description = "电费系统密码（加密存储）")
    private String dormPassword;
    @Schema(description = "是否公开电费信息（0=否, 1=是）")
    private Integer dormPublic;
    @Schema(description = "是否已认证（0=否, 1=是）")
    private Integer verified;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
