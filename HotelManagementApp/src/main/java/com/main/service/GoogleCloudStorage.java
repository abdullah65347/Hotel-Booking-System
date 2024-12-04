package com.main.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.main.exception.OurException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class GoogleCloudStorage {

    @Value("${gcs.bucket-name}")
    private String bucketName;

    @Value("${gcs.credentials.file}")
    private Resource credentialsFile;
    
    public String saveImageToS3(MultipartFile photo) {
        try {
            // Load credentials from the specified file
            InputStream credentialsStream = credentialsFile.getInputStream();
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            
            // Initialize the GCS client
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();

            // Define the file name and metadata
            String gcsFilename = photo.getOriginalFilename();
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, gcsFilename)
                    .setContentType(photo.getContentType()) // e.g., image/jpeg
                    .build();

            // Upload the file bytes to GCS
            storage.create(blobInfo, photo.getBytes());

            // Construct and return the public URL
            return String.format("https://storage.googleapis.com/%s/%s", bucketName, gcsFilename);

        } catch (IOException e) {
            e.printStackTrace();
            throw new OurException("Unable to upload image to GCS bucket: " + e.getMessage());
        }
    }
}
