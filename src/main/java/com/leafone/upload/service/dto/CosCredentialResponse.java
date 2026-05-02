package com.leafone.upload.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "COS上传凭证响应")
public class CosCredentialResponse {
    @Schema(description = "上传地址")
    private String uploadUrl;
    @Schema(description = "存储桶名称")
    private String bucket;
    @Schema(description = "地域", example = "ap-shanghai")
    private String region;
    @Schema(description = "临时SecretId")
    private String secretId;
}
