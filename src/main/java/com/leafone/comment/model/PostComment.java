package com.leafone.comment.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post_comments")
@Schema(description = "帖子评论")
public class PostComment {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "评论ID")
    private Long id;
    @Schema(description = "帖子ID")
    private Long postId;
    @Schema(description = "评论者ID")
    private Long userId;
    @Schema(description = "父评论ID（顶级评论为空）")
    private Long parentId;
    @Schema(description = "被回复用户ID")
    private Long replyToUserId;
    @Schema(description = "评论内容")
    private String content;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "状态（1=正常, 3=已删除）")
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private LocalDateTime deletedAt;
}
