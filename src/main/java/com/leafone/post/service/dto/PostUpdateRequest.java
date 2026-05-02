package com.leafone.post.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "编辑帖子请求")
public class PostUpdateRequest {
    @Schema(description = "版块ID")
    private Long moduleId;

    @Size(max = 120, message = "标题不能超过120字")
    @Schema(description = "帖子标题")
    private String title;

    @Size(max = 255, message = "摘要不能超过255字")
    @Schema(description = "帖子摘要")
    private String summary;

    @Size(max = 50000, message = "正文内容过长")
    @Schema(description = "帖子正文内容")
    private String content;

    @Size(max = 512, message = "封面URL过长")
    @Schema(description = "封面图URL")
    private String coverUrl;

    @Size(max = 128, message = "定位名称过长")
    @Schema(description = "定位")
    private String locationName;

    @Schema(description = "附件列表（文件直链）")
    private List<AttachmentRequest> attachments;
}
