package com.hunmin.global.s3;

import com.hunmin.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Component
public class S3FileUploader {
    private final String bucket;
    private final String region;
    private final S3Client s3Client;
    private final String baseUrl;

    public S3FileUploader(
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${cloud.aws.region.static}") String region,
            S3Client s3Client
    ) {
        this.bucket = bucket;
        this.region = region;
        this.s3Client = s3Client;
        this.baseUrl = String.format("https://%s.s3.%s.amazonaws.com", bucket, region);
    }

    public String uploadImage(MultipartFile multipartFile) throws IOException {

        // 파일 검증
        FileValidate.validateImageFile(multipartFile);

        // 파일명 추출 및 변경
        String originalFileName = multipartFile.getOriginalFilename();
        String fileName = FileValidate.createUniqueFileName(originalFileName);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(multipartFile.getContentType())
                    .build();

            RequestBody requestBody = RequestBody.fromInputStream(
                    multipartFile.getInputStream(),
                    multipartFile.getSize()
            );
            s3Client.putObject(putObjectRequest, requestBody);
            return getFile(fileName);
        } catch (Exception e) {
            throw ErrorCode.IMAGE_FILE_UPLOAD_FAIL.throwException();
        }
    }

    public String getFile(String filename) {
        return baseUrl + "/" + filename;
    }

    public void delete(String filename) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(filename)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}
