package com.leafone.post.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "附件请求")
public class AttachmentRequest {
    @NotBlank
    @Schema(description = "文件显示名称", example = "数据结构期末复习.pdf")
    private String displayName;

    @NotBlank
    @Schema(description = "文件直链URL", example = "https://pan.baidu.com/file/xxx")
    private String fileUrl;

    @Schema(description = "文件大小", example = "2.3MB")
    private String fileSize;

    @Schema(description = "文件类型（pdf/doc/excel/ppt/zip/other）", example = "pdf")
    private String fileType;
}
