package com.streaming_app.VideoService.Application.CQRS.Features.Videos.Handlers.Commands;

import com.azure.storage.blob.BlobContainerClient;
import com.streaming_app.VideoService.Application.CQRS.Features.Videos.Requests.Commands.UploadMovieCommand;
import com.streaming_app.VideoService.Application.Dtos.Requests.UploadMovieRequestDto;
import com.streaming_app.VideoService.Application.Dtos.Responses.UploadVideoResponseDto;
import com.streaming_app.VideoService.Application.Events.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadVideoCommandHandler {

    private final KafkaTemplate<String, VideoUploadedEvent> kafkaTemplate;
    //private final BlobClient blobClient;
    private final BlobContainerClient blobContainerClient;

    private static final String VIDEO_UPLOADED_TOPIC = "video.uploaded";

    /**
     *
     * Upload video to Azure BlobStorage
     * Publish VideoUploadedEvent to Kafka
     *
     * FLOW:
     * 1. Receive multipart video file
     * 2. Generate unique video key
     * 3. Upload to Blob Storage
     * 4. Publish VideoUploadedEvent to kafka
     * 5. Encoding service consumes the event
     */
    public UploadVideoResponseDto handle(UploadMovieCommand request)
    {
        var videoKey = "";
        var requestData = new UploadMovieRequestDto(
                request.moviePublicId(),
                request.file()
        );

        var file = requestData.getFile();
        var originalFileName = file.getOriginalFilename();
        var moviePublicId = requestData.getMoviePublicId();
        var fileSize = file.getSize();

        log.info(
                "Starting video upload for movieId: {} file size: {}MB",
                moviePublicId,
                file.getSize() / (1024 * 1024)
        );

        log.info(
                "Starting video upload for movieId: {} file name: {}",
                moviePublicId,
                file.getOriginalFilename()
        );


        if (file.isEmpty()) {
            throw new IllegalArgumentException("Video file cannot be empty");
        }

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Video file name is required");
        }

        // Generate unique Blob-storage key for raw video
        // Format: example:/ raw/d634a44b-81f1-4e09-8dd2-192542e8473b/original.mp4

        videoKey =
                "raw/"
                + moviePublicId
                + "/"
                + originalFileName;

        // build the request and send to Blob-storage
        try
        {
            var blobClient = blobContainerClient.getBlobClient(videoKey);

            log.info(
                    "Uploading video to Azure Blob-storage. videoKey={}",
                    videoKey
            );

            /*
             * Upload the MultipartFile directly to Azure Blob Storage.
             */

            try (var inputStream = file.getInputStream()) {

                blobClient.upload(
                        inputStream,
                        file.getSize(),
                        true
                );
            }

            log.info(
                    "Video uploaded successfully to Azure Blob Storage. videoKey={}",
                    videoKey
            );

            /*
             * Create Kafka event.
             */

            var event = new VideoUploadedEvent(
                    moviePublicId,
                    videoKey,
                    originalFileName,
                    fileSize
            );

            /*
             * Publish event to Kafka.
             */

            kafkaTemplate.send(
                    VIDEO_UPLOADED_TOPIC,
                    moviePublicId.toString(),
                    event
            );
            log.info(
                    "VideoUploadedEvent published to Kafka. moviePublicId={}, videoKey={}",
                    moviePublicId,
                    videoKey
            );

            return new UploadVideoResponseDto(videoKey);
        } catch (Exception e) {

            log.error(
                    "Video upload failed for movieId: {}",
                    moviePublicId,
                    e
            );
            throw new RuntimeException(
                    "Failed to upload video",
                    e
            );
        }
    }
}
