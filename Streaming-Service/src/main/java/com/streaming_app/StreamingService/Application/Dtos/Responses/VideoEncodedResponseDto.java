package com.streaming_app.StreamingService.Application.Dtos.Responses;

public record VideoEncodedResponseDto(
        String MoviePublicId,
        String StreamingUrl,        // Presigned HLS Master playlist Url
        String Quality,            // available qualities
        long ExpiredInMinutes      // URL expiry time
) {
}
