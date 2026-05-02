package com.leafone.post.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "帖子详情响应")
public class PostDetailResponse {
    @Schema(description = "帖子ID")
    private Long id;
    @Schema(description = "作者ID")
    private Long authorId;
    @Schema(description = "作者昵称")
    private String authorNickname;
    @Schema(description = "作者头像URL")
    private String authorAvatarUrl;
    @Schema(description = "版块ID")
    private Long moduleId;
    @Schema(description = "版块名称")
    private String moduleName;
    @Schema(description = "帖子标题")
    private String title;
    @Schema(description = "帖子摘要")
    private String summary;
    @Schema(description = "帖子正文内容（Markdown 格式）")
    private String content;
    @Schema(description = "封面图URL")
    private String coverUrl;
    @Schema(description = "封面类型")
    private String coverType;
    @Schema(description = "价格")
    private BigDecimal price;
    @Schema(description = "地点名称")
    private String locationName;
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
    @Schema(description = "当前用户是否已点赞")
    private boolean liked;
    @Schema(description = "当前用户是否已收藏")
    private boolean favorited;
    @Schema(description = "附件列表")
    private List<AttachmentResponse> attachments;
}
