package com.leafone.post.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("home_headlines")
@Schema(description = "首页头条")
public class HomeHeadline {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "头条ID")
    private Long id;
    @Schema(description = "标题")
    private String title;
    @Schema(description = "摘要")
    private String summary;
    @Schema(description = "封面图URL")
    private String coverUrl;
    @Schema(description = "关联目标类型")
    private String targetType;
    @Schema(description = "关联目标ID")
    private Long targetId;
    @Schema(description = "外部链接URL")
    private String externalUrl;
    @Schema(description = "是否置顶（0=否, 1=是）")
    private Integer isPinned;
    @Schema(description = "浏览数")
    private Integer viewCount;
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
