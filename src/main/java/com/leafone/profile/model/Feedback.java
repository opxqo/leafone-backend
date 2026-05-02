package com.leafone.profile.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedbacks")
@Schema(description = "用户反馈")
public class Feedback {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "反馈ID")
    private Long id;
    @Schema(description = "提交者ID")
    private Long userId;
    @Schema(description = "反馈内容")
    private String content;
    @Schema(description = "联系方式")
    private String contact;
    @Schema(description = "处理状态", example = "PENDING")
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
