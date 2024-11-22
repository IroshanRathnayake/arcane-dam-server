package com.arcane.dam.controller;

import com.arcane.dam.dto.FileUploadResponse;
import com.arcane.dam.exception.CustomException;
import com.arcane.dam.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping (value = "/api/v1/asset")
@CrossOrigin
@RequiredArgsConstructor
public class AssetController {
    private final AssetService assetService;

    @PreAuthorize("hasRole('admin') or hasRole('team_member')")
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException("File is empty", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(assetService.uploadFile(file), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) {
        byte[] fileContent = assetService.downloadFile(fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileId)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }

    @PreAuthorize("hasRole('admin') or hasRole('team_member')")
    @GetMapping("/view/{fileId}")
    public ResponseEntity<byte[]> viewFile(@PathVariable String fileId) {
        byte[] fileContent = assetService.downloadFile(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileId)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }

    @PreAuthorize("hasRole('admin') or hasRole('team_member')")
    @PutMapping("/update/{fileId}")
    public ResponseEntity<FileUploadResponse> updateFile(@PathVariable String fileId, @RequestBody MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException("File is empty", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(assetService.updateFile(fileId, file),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('admin') or hasRole('team_member')")
    @DeleteMapping("/delete/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileId) {
        if(assetService.deleteFile(fileId)){
            return new ResponseEntity<>("File deleted successfully", HttpStatus.OK);
        }
        throw new CustomException("File not found", HttpStatus.NOT_FOUND);
    }
}
