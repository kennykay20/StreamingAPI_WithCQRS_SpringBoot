package com.streaming_app.ContentService.Application.Events;

import java.util.UUID;

public record VideoUploadedEvent(
        UUID moviePublicId,
        String videoKey,
        String fileName,
        long fileSizeByBytes
) {
}
