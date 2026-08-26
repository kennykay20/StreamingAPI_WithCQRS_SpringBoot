package com.streaming_app.StreamingService.Infrastructure.Services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces.IMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService implements IMovieService {

    private final BlobContainerClient encodedBlobContainerClient;
    /*
     * Generate a presigned(streaming) URL for the Azure Blob.
     *
     * The key is the actual blob key:
     *
     * encoded/{movieId}/master.m3u8
     */
    @Override
    public String GeneratePresignedUrl(
            String blobKey,
            long presignedUrlExpiry
    ) {

        log.info(
                "Generating presigned(streaming) SAS URL for blob: {}",
                blobKey
        );

        BlobClient blobClient =
                encodedBlobContainerClient
                        .getBlobClient(blobKey);

        OffsetDateTime expiryTime = OffsetDateTime.now()
                .plus(
                        presignedUrlExpiry,
                        ChronoUnit.MILLIS
                );

        BlobSasPermission permission = new BlobSasPermission()
                .setReadPermission((true));

        BlobServiceSasSignatureValues sasSignatureValues =
                new BlobServiceSasSignatureValues(
                        expiryTime,
                        permission
                );

        String sasToken =
                blobClient.generateSas(
                        sasSignatureValues
                );

        return blobClient.getBlobUrl()
                + "?"
                + sasToken;
    }
}
