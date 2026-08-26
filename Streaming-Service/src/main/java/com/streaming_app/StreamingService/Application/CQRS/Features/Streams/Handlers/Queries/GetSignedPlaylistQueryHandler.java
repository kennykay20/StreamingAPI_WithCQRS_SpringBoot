package com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Handlers.Queries;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Requests.Queries.GetSignedPlaylistQuery;
import com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces.IMovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GetSignedPlaylistQueryHandler {

    private final IMovieService movieService;

    private final BlobContainerClient encodedBlobContainerClient;

    @Value("${azure.storage.presigned-url-expiry}")
    private long presignedUrlExpiry;

    public GetSignedPlaylistQueryHandler(
        IMovieService movieService,
        @Qualifier("encodedBlobContainerClient")
        BlobContainerClient encodedBlobContainerClient
    ){
        this.movieService = movieService;
        this.encodedBlobContainerClient = encodedBlobContainerClient;
    }


    /**
     * This is the key method that makes everything secure
     * Reads an HLS playlist and rewrites every referenced
     * playlist or segment with a signed Azure SAS Url.
     */
    public String handle(GetSignedPlaylistQuery request) {



        String movieId =
                request.requestDto()
                        .getMoviePublicId()
                        .toString();
        /*
         * get the basePath from te path that is the playlistPath
         */
        String playlistPath =
                request.requestDto()
                        .getPath();

        log.info(
                "Generating signed playlist for movieId: {}, path: {}",
                movieId,
                playlistPath
        );

        /*
         * Basic validation.
         *
         * We expect something like:
         *
         * encoded/{movieId}/720p/playlist.m3u8
         */

        if (playlistPath == null || playlistPath.isBlank()) {

            throw new IllegalArgumentException(
                    "Playlist path cannot be empty"
            );
        }

        if (!playlistPath.startsWith(
                "encoded/" + movieId + "/"
        )) {

            throw new IllegalArgumentException(
                    "Invalid playlist path for movie"
            );
        }

        /*
         * Get the base path.
         *
         * Example:
         *
         * encoded/{movieId}/720p/playlist.m3u8
         *
         * becomes:
         *
         * encoded/{movieId}/720p/
         */

        int lastSlash = playlistPath.lastIndexOf('/');

        String basePath =
                playlistPath.substring(
                        0,
                        lastSlash + 1
        );

        log.info(
                "BasePath from movieId: {}, and basePath: {}",
                movieId,
                basePath
        );

        /*
         * Read the actual .m3u8 content from Azure blob storage
         */

        String m3u8Content =
                readFromBlob(playlistPath);
        /*
         * Rewrite segment references with signed URLs.
         */

        return reWriteM3u8SignedUrls(
                m3u8Content,
                basePath
        );

    }

    /**
     * Read playlist content from Azure Blob Storage.
     */
    private String readFromBlob(String playlistPath)
    {
        log.info(
                "Reading playlist from Azure Blob Storage: {}",
                playlistPath
        );

        BlobClient blobClient =
                encodedBlobContainerClient
                        .getBlobClient(playlistPath);

        if (!blobClient.exists()) {
            log.error(
                    "Playlist not found in Blob Storage: {}",
                    playlistPath
            );

            throw new IllegalArgumentException(
                    "Playlist not found"
            );
        }

        String content =
                blobClient
                        .downloadContent()
                        .toString();

        log.info(
                "Playlist read successfully from Blob Storage: {}",
                playlistPath
        );
        return content;
    }

    /**
     * Rewrite each segment reference in the playlist
     * with a temporary signed SAS URL.
     */
    private String reWriteM3u8SignedUrls(
            String masterContent,
            String basePath
    ) {
        StringBuilder reWritten = new StringBuilder();

        for (String line : masterContent.split("\\r?\\n")) {

            String trimmed = line.trim();

            /*
             * Keep:
             *
             * #EXTM3U
             * #EXT-X-VERSION
             * #EXTINF
             * etc.
             */

            // Skip empty lines and comments
            if(trimmed.isEmpty() || trimmed.startsWith("#")) {
                reWritten.append(line)
                         .append("\n");
                continue;
            }

            /*
             * This is a segment or playlist reference
             * build full Blob key and sign it
             * Example:
             * segment_000.ts
             * becomes:
             * encoded/{movieId}/720p/segment_000.ts
             */

            String fullKey = basePath + trimmed;
            String signedUrl =
                    movieService.GeneratePresignedUrl(
                            fullKey,
                            presignedUrlExpiry
                    );

            reWritten.append(signedUrl)
                     .append("\n");
        }

        return  reWritten.toString();
    }
}
