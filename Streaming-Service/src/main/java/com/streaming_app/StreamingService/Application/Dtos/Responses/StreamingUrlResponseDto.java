package com.streaming_app.StreamingService.Application.Dtos.Responses;

public record StreamingUrlResponseDto(
        String MoviePublicId,
        String StreamingUrl,        // Presigned HLS Master playlist Url
        String Quality,            // available qualities
        long ExpiredInMinutes
) {
}
