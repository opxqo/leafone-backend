package com.leafone.upload.service;

import com.leafone.upload.service.dto.CosCredentialResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UploadService {

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
        // TODO: save file record to database
    }
}
