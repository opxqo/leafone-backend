package com.leafone.post.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post_attachments")
@Schema(description = "帖子附件")
public class PostAttachment {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "附件ID")
    private Long id;
    @Schema(description = "帖子ID")
    private Long postId;
    @Schema(description = "显示名称")
    private String displayName;
    @Schema(description = "文件直链URL")
    private String fileUrl;
    @Schema(description = "文件大小", example = "2.3MB")
    private String fileSize;
    @Schema(description = "文件类型", example = "pdf")
    private String fileType;
    @Schema(description = "排序序号")
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
