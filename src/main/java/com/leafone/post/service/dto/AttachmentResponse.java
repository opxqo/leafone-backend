package com.leafone.post.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "附件响应")
public class AttachmentResponse {
    @Schema(description = "附件ID")
    private Long id;
    @Schema(description = "文件显示名称")
    private String displayName;
    @Schema(description = "文件直链URL")
    private String fileUrl;
    @Schema(description = "文件大小")
    private String fileSize;
    @Schema(description = "文件类型")
    private String fileType;
}
