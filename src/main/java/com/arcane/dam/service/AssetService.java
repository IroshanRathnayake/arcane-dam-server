package com.arcane.dam.service;

import com.arcane.dam.dto.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface AssetService {
    FileUploadResponse uploadFile(MultipartFile multipartFile);
    byte[] downloadFile(String fileId);
    FileUploadResponse updateFile(String fileId, MultipartFile multipartFile);
    boolean deleteFile(String fileId);
}
