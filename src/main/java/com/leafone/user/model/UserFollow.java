package com.leafone.user.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_follows")
@Schema(description = "用户关注关系")
public class UserFollow {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "记录ID")
    private Long id;
    @Schema(description = "关注者ID")
    private Long followerId;
    @Schema(description = "被关注者ID")
    private Long followingId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableLogic
    private LocalDateTime deletedAt;
}
