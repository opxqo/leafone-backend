package com.leafone.message.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("messages")
@Schema(description = "消息通知")
public class Message {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "消息ID")
    private Long id;
    @Schema(description = "接收者ID")
    private Long userId;
    @Schema(description = "发送者ID")
    private Long senderId;
    @Schema(description = "消息类型", example = "SYSTEM")
    private String type;
    @Schema(description = "消息标题")
    private String title;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "关联目标类型（POST/COMMENT）")
    private String targetType;
    @Schema(description = "关联目标ID")
    private Long targetId;
    @Schema(description = "已读时间（null=未读）")
    private LocalDateTime readAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableLogic
    private LocalDateTime deletedAt;
}
