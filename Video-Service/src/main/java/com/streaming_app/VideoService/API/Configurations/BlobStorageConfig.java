package com.streaming_app.VideoService.API.Configurations;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.servlet.autoconfigure.MultipartProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BlobStorageConfig {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @Bean
    public BlobServiceClient blobServiceClient() {


        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    @Bean
    public BlobContainerClient blobContainerClient(
            BlobServiceClient blobServiceClient
    ) {
        BlobContainerClient containerClient =
                blobServiceClient.getBlobContainerClient(containerName);

        if (!containerClient.exists()) {
            containerClient.create();
        }

        return containerClient;
    }


    @Bean
    CommandLineRunner logMultipartConfiguration(
            MultipartProperties multipartProperties) {

        return args -> {
            log.info("======================================");
            log.info("MAX FILE SIZE: {}", multipartProperties.getMaxFileSize());
            log.info("MAX REQUEST SIZE: {}", multipartProperties.getMaxRequestSize());
            log.info("======================================");
        };
    }
}
