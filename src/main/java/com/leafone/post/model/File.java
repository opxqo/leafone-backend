package com.leafone.post.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("files")
@Schema(description = "文件")
public class File {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文件ID")
    private Long id;
    @Schema(description = "上传者ID")
    private Long ownerId;
    @Schema(description = "存储桶名称")
    private String bucket;
    @Schema(description = "对象键")
    private String objectKey;
    @Schema(description = "文件访问URL")
    private String url;
    @Schema(description = "MIME类型", example = "image/jpeg")
    private String mimeType;
    @Schema(description = "文件类型（image/file）")
    private String fileType;
    @Schema(description = "文件大小（字节）")
    private Long sizeBytes;
    @Schema(description = "SHA256哈希值")
    private String sha256;
    @Schema(description = "状态（1=正常）")
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
