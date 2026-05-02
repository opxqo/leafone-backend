package com.leafone.reaction.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reactions")
@Schema(description = "互动记录")
public class Reaction {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "记录ID")
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "目标类型（POST/COMMENT）", example = "POST")
    private String targetType;
    @Schema(description = "目标ID")
    private Long targetId;
    @Schema(description = "互动类型（LIKE/FAVORITE）", example = "LIKE")
    private String reactionType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableLogic
    private LocalDateTime deletedAt;
}
