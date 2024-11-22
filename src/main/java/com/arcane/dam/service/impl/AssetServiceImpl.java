package com.arcane.dam.service.impl;

import com.arcane.dam.dto.FileUploadResponse;
import com.arcane.dam.entity.AssetMetaData;
import com.arcane.dam.exception.CustomException;
import com.arcane.dam.repository.AssetRepository;
import com.arcane.dam.service.AssetService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AssetServiceImpl implements AssetService {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.access_key}")
    private String accessKey;

    @Value("${aws.secret_key}")
    private String secretKey;

    private S3Client s3Client;
    private final AssetRepository assetRepository;
    private final ModelMapper mapper;

    @PostConstruct
    private void initialize() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(software.amazon.awssdk.regions.Region.US_EAST_1)
                .build();
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile multipartFile) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDate = dateTimeFormatter.format(LocalDate.now());
        String filePath = todayDate + "/" + multipartFile.getOriginalFilename();
        String fileId = UUID.randomUUID().toString();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, filePath);

            // Save metadata to DynamoDB
            AssetMetaData metadata = new AssetMetaData(
                    fileId,
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType(),
                    multipartFile.getSize(),
                    fileUrl,
                    Instant.now()
            );
            assetRepository.save(metadata);

            return mapper.map(metadata, FileUploadResponse.class);

        } catch (IOException e) {
            log.error("Error occurred ==> {}", e.getMessage());
            throw new CustomException("Error occurred in file upload: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public byte[] downloadFile(String fileId) {
        AssetMetaData assetMetaData = assetRepository.findById(fileId)
                .orElseThrow(() -> new CustomException("File not found", HttpStatus.NOT_FOUND));

        String fileUrl = assetMetaData.getFileUrl();
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new CustomException("File URL is empty", HttpStatus.BAD_REQUEST);
        }

        String filePath = fileUrl.replace("https://arcane-bucket.s3.amazonaws.com/", "");

        if (filePath.isEmpty()) {
            throw new CustomException("File path is empty", HttpStatus.BAD_REQUEST);
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

        return objectBytes.asByteArray();
    }


    @Override
    public FileUploadResponse updateFile(String fileId, MultipartFile multipartFile) {
        AssetMetaData assetMetaData = assetRepository.findById(fileId)
                .orElseThrow(() -> new CustomException("File not found", HttpStatus.NOT_FOUND));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayDate = dateTimeFormatter.format(LocalDate.now());
        String filePath = todayDate + "/" + multipartFile.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, filePath);

            // Update metadata in DynamoDB
            assetMetaData.setFileName(multipartFile.getOriginalFilename());
            assetMetaData.setFileUrl(fileUrl);
            assetMetaData.setFileSize(multipartFile.getSize());
            assetMetaData.setUploadTimestamp(Instant.now());

            if(assetRepository.update(assetMetaData.getId(),assetMetaData)){
                return mapper.map(assetMetaData, FileUploadResponse.class);
            }

        } catch (IOException e) {
            throw new CustomException("Error occurred in file upload: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        throw new CustomException("Error occurred in file upload", HttpStatus.BAD_REQUEST);
    }

    @Override
    public boolean deleteFile(String fileId) {
        AssetMetaData assetMetaData = assetRepository.findById(fileId)
                .orElseThrow(() -> new CustomException("File not found", HttpStatus.NOT_FOUND));

        String filePath = assetMetaData.getFileUrl().replace("https://arcane-bucket.s3.amazonaws.com/", "");

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            return assetRepository.delete(assetMetaData);

        } catch (Exception e) {
            throw new CustomException("Error occurred while deleting the file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
