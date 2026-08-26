package com.streaming_app.StreamingService.API.Configurations;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BlobStorageConfig {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.raw-container-name}")
    private String rawContainerName;

    @Value("${azure.storage.encoded-container-name}")
    private String encodedContainerName;

    @Bean
    public BlobServiceClient blobServiceClient() {
        if(connectionString != null)
        {
            log.info("ConnectionString is not null ");
        }
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    @Bean("rawBlobContainerClient")
    public BlobContainerClient rawBlobContainerClient(
            BlobServiceClient blobServiceClient) {

        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(rawContainerName);

        if (!containerClient.exists()) {
            containerClient.create();
        }

        return containerClient;
    }

    @Bean("encodedBlobContainerClient")
    public BlobContainerClient encodedBlobContainerClient(
            BlobServiceClient blobServiceClient) {

        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(encodedContainerName);

        if (!containerClient.exists()) {
            containerClient.create();
        }

        return containerClient;
    }
}
