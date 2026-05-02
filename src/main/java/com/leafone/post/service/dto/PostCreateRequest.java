package com.leafone.post.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "发布帖子请求")
public class PostCreateRequest {
    @NotNull
    @Schema(description = "版块ID", example = "1")
    private Long moduleId;

    @NotBlank
    @Schema(description = "帖子标题", example = "Python期末复习笔记分享")
    private String title;

    @NotBlank
    @Schema(description = "帖子摘要", example = "整理了本学期的重点知识")
    private String summary;

    @NotBlank
    @Schema(description = "帖子正文内容（Markdown 格式，支持标题/加粗/斜体/列表/引用/链接/分割线）")
    private String content;

    @Schema(description = "封面图URL")
    private String coverUrl;

    @Schema(description = "定位（非必须）", example = "图书馆")
    private String locationName;

    @Schema(description = "附件列表（非必须，文件直链）")
    private List<AttachmentRequest> attachments;
}
