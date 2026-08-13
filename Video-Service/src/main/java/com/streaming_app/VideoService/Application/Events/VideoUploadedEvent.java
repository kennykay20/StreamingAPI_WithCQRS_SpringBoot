package com.streaming_app.VideoService.Application.Events;

import java.util.UUID;

/**
 *
 * Event published to kafka when a video is uploaded to Blob-Storage
 * Encoding Service consume this to start FFmpeg processing
 *
 *
 */

public record VideoUploadedEvent(
        UUID moviePublicId,
        String videoKey,
        String fileName,
        long fileSizeByBytes
) {
}
