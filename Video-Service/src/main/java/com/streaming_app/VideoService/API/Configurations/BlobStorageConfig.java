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

        log.info("Azure connection string is null: {}",
                connectionString == null);

        log.info("Azure connection string length: {}",
                connectionString == null ? 0 : connectionString.length());

        if (connectionString != null) {

            String[] parts = connectionString.split(";");

            for (String part : parts) {

                if (part.startsWith("DefaultEndpointsProtocol=")) {
                    log.info("Protocol part: {}", part);
                }

                if (part.startsWith("AccountName=")) {
                    log.info("AccountName part: {}", part);
                }

                if (part.startsWith("AccountKey=")) {
                    String key = part.substring("AccountKey=".length());

                    log.info("AccountKey length: {}", key.length());
                    log.info("AccountKey contains backslash: {}", key.contains("\\"));
                    log.info("AccountKey contains space: {}", key.contains(" "));
                    log.info("AccountKey starts with quote: {}", key.startsWith("\""));
                    log.info("AccountKey ends with quote: {}", key.endsWith("\""));
                }

                if (part.startsWith("EndpointSuffix=")) {
                    log.info("EndpointSuffix part: {}", part);
                }
            }
        }
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
