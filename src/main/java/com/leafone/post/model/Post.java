package com.leafone.post.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("posts")
@Schema(description = "帖子")
public class Post {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "帖子ID")
    private Long id;
    @Schema(description = "作者ID")
    private Long authorId;
    @Schema(description = "版块ID")
    private Long moduleId;
    @Schema(description = "帖子标题")
    private String title;
    @Schema(description = "帖子摘要")
    private String summary;
    @Schema(description = "帖子正文")
    private String content;
    @Schema(description = "封面图URL")
    private String coverUrl;
    @Schema(description = "封面类型")
    private String coverType;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "地点名称")
    private String locationName;
    @Schema(description = "状态（1=正常, 2=隐藏, 3=删除）")
    private Integer status;
    @Schema(description = "是否置顶（0=否, 1=是）")
    private Integer isPinned;
    @Schema(description = "是否精选（0=否, 1=是）")
    private Integer isFeatured;
    @Schema(description = "浏览数")
    private Integer viewCount;
    @Schema(description = "分享数")
    private Integer shareCount;
    @Schema(description = "评论数")
    private Integer commentCount;
    @Schema(description = "点赞数")
    private Integer likeCount;
    @Schema(description = "收藏数")
    private Integer favoriteCount;
    @Schema(description = "发布时间")
    private LocalDateTime publishedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private LocalDateTime deletedAt;
}
