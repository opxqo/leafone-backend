package com.leafone.upload.service;

import com.leafone.post.mapper.FileMapper;
import com.leafone.post.model.File;
import com.leafone.upload.service.dto.CosCredentialResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final FileMapper fileMapper;

    @Value("${leafone.cos.secret-id:}")
    private String cosSecretId;

    @Value("${leafone.cos.secret-key:}")
    private String cosSecretKey;

    @Value("${leafone.cos.bucket:}")
    private String cosBucket;

    @Value("${leafone.cos.region:ap-shanghai}")
    private String cosRegion;

    public CosCredentialResponse getCosCredential(String fileName, String fileType, Long userId) {
        CosCredentialResponse resp = new CosCredentialResponse();
        resp.setUploadUrl("https://" + cosBucket + ".cos." + cosRegion + ".myqcloud.com/" + userId + "/" + fileName);
        resp.setBucket(cosBucket);
        resp.setRegion(cosRegion);
        resp.setSecretId(cosSecretId);
        return resp;
    }

    public void completeUpload(Long fileId, String url, Long userId) {
        File file = fileMapper.selectById(fileId);
        if (file == null) {
            file = new File();
            file.setId(fileId);
            file.setOwnerId(userId);
            file.setBucket(cosBucket);
            file.setUrl(url);
            file.setStatus(1);
            fileMapper.insert(file);
        } else {
            file.setUrl(url);
            file.setStatus(1);
            fileMapper.updateById(file);
        }
    }
}
