package com.streaming_app.EncodingService.Application.Events;

import java.util.UUID;

public record VideoUploadedEvent(
        UUID moviePublicId,
        String videoKey,
        String fileName,
        long fileSizeByBytes
) {
}
