package com.leafone.upload.controller;

import com.leafone.common.response.R;
import com.leafone.common.util.AuthUtil;
import com.leafone.upload.service.UploadService;
import com.leafone.upload.service.dto.CosCredentialResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "文件上传", description = "文件上传凭证获取和上传完成确认")
@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @Operation(summary = "获取上传凭证", description = "获取腾讯云 COS 临时上传凭证，需登录")
    @PostMapping("/cos-credential")
    public R<CosCredentialResponse> getCosCredential(
            @Parameter(description = "文件名") @RequestParam String fileName,
            @Parameter(description = "文件类型（image/file）") @RequestParam(defaultValue = "image") String fileType,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(uploadService.getCosCredential(fileName, fileType, userId));
    }

    @Operation(summary = "确认上传完成", description = "文件上传完成后确认，需登录")
    @PostMapping("/{fileId}/complete")
    public R<Void> completeUpload(
            @Parameter(description = "文件ID") @PathVariable Long fileId,
            @Parameter(description = "文件访问URL") @RequestParam String url,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        uploadService.completeUpload(fileId, url, userId);
        return R.ok();
    }
}
