package com.leafone.post.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "编辑帖子请求")
public class PostUpdateRequest {
    @Schema(description = "版块ID")
    private Long moduleId;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子摘要")
    private String summary;

    @Schema(description = "帖子正文内容")
    private String content;

    @Schema(description = "封面图URL")
    private String coverUrl;

    @Schema(description = "定位")
    private String locationName;

    @Schema(description = "附件列表（文件直链）")
    private List<AttachmentRequest> attachments;
}
