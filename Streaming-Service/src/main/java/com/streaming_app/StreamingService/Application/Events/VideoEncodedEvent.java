package com.streaming_app.StreamingService.Application.Events;

import java.util.UUID;

public record VideoEncodedEvent(
        UUID moviePublicId,
        String hlsUrl,             // Master playlist URL for streaming
        String masterPlaylistKey,  // Blob key for master.m3u8
        boolean success,           //
        String errorMessage        // If encoding failed
) {
}
