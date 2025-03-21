package com.rafiqstore.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String itemName) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Generate a unique file name
        String fileName = itemName + "_" + UUID.randomUUID() + ".jpg";

        // Upload the file to R2
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                        file.getInputStream(), file.getSize())
        );

        // Return the file URL
        return "https://" + bucketName + ".r2.cloudflarestorage.com/" + fileName;
    }
}
