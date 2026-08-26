package com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Handlers.Queries;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Requests.Queries.GetStreamingURLQuery;
import com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces.ICacheService;
import com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces.IMovieService;
import com.streaming_app.StreamingService.Application.Dtos.Requests.StreamingUrlRequestDto;
import com.streaming_app.StreamingService.Application.Dtos.Responses.StreamingUrlResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GetStreamingURLQueryHandler {

    private final static String MASTER_PLAYLIST_KEY_PREFIX = "streaming:playlist:";
    private final static String STREAMING_URL_CACHE_PREFIX = "streaming:url:";
    private final static String VIDEO_QUALITIES = "1080p, 720p, 480p, 360p";
      // 60 mins
    private final ICacheService cacheService;
    private final IMovieService movieService;

    private final BlobContainerClient encodedBlobContainerClient;

    @Value("${azure.storage.presigned-url-expiry}")
    private long presignedUrlExpiry;

    public GetStreamingURLQueryHandler(
            ICacheService cacheService,
            IMovieService movieService,
            @Qualifier("encodedBlobContainerClient")
            BlobContainerClient encodedBlobContainerClient
    )
    {
        this.cacheService = cacheService;
        this.movieService = movieService;
        this.encodedBlobContainerClient = encodedBlobContainerClient;
    }

    /*
     * Get Streaming URL for a movie
     *
     * FLOW:
     *
     * 1. Check Redis cache for existing presigned(streaming) URL
     * 2. If Cached(found) - return it
     * 3. Get master playlist key from Redis
     * 4. Generate new presigned URL from Azure Blob storage
     * 5. Cache the URL in Redis
     * 6. Return the presigned(streaming) URL
     *
     *  Why presigned(streaming) URL?
     * - Blob storage is private locker room - videos are not publicly accessible
     * - Presigned(Streaming) URL gives temporary access (X minutes)
     * - Prevent unauthorized video downloads
     *
     */
    public StreamingUrlResponseDto handle(GetStreamingURLQuery request) {

        UUID movieId = request.requestDto().getMoviePublicId();

        log.info(
                "Streaming request for movieId: {} ",
                movieId
        );

        String streamingUrl = "";

        String cacheKey = STREAMING_URL_CACHE_PREFIX + movieId;
        String playlistKey = MASTER_PLAYLIST_KEY_PREFIX + movieId;

        /*
         * Step 1:
         * Check cached streaming URL.
         */
        String cacheUrl = cacheService.getCacheValue(cacheKey);

        if(cacheUrl != null)
        {
            log.info(
                    "Returning cache streaming URL for movieId: {} ",
                    movieId
            );
            return new StreamingUrlResponseDto(
                    movieId.toString(),
                    cacheUrl,
                    VIDEO_QUALITIES,
                    presignedUrlExpiry
            );
        }

        /*
         * Step 2:
         * Get the actual Azure master playlist key.
         */
        String masterPlaylistKey =
                cacheService.getCacheValue(playlistKey);

        if (masterPlaylistKey == null)
        {
            log.warn(
                    "Master playlist key not found for movieId: {}",
                    movieId
            );
            throw new IllegalArgumentException(
                    "Streaming information not found for movieId: "
                    + movieId
            );
        }

        /*
         * Step 3:
         * Generate a new presigned(streaming) URL from Azure Blob storage.
         */

        log.info(
                "Generate new presigned URL for movie: {}",
                movieId
        );
        streamingUrl = movieService.GeneratePresignedUrl(masterPlaylistKey, presignedUrlExpiry);

        /*
         * Step 4:
         * Cache for 55 minutes.
         *
         * Actual Presigned(streaming) URL expiry = 60 minutes.
         *
         * This avoids returning a presigned(streaming) URL that is about to expire.
         */
        cacheService.cacheValue(
                cacheKey,
                streamingUrl,
                55,
                TimeUnit.MINUTES
        );

        log.info(
                "Streaming URL generated and cache for movie: {}",
                movieId
        );

        return new StreamingUrlResponseDto(
                movieId.toString(),
                streamingUrl,
                VIDEO_QUALITIES,
                presignedUrlExpiry
        );
    }

    /*
     * Invalidate cached streaming URL.
     *
     * Useful when the video is re-encoded.
     */


    private void invalidateStreamingUrl(
            UUID moviePublicId
    ) {

        String cacheKey =
                STREAMING_URL_CACHE_PREFIX
                        + moviePublicId;

        cacheService.delete(cacheKey);

        log.info(
                "Streaming URL cache invalidated for movieId: {}",
                moviePublicId
        );
    }

}
