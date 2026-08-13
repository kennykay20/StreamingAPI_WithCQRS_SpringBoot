package com.streaming_app.EncodingService.Application.CQRS.Features.Encode.Requests.Commands;

import com.streaming_app.EncodingService.Application.Events.VideoUploadedEvent;

public record EncodeVideoCommand(
    VideoUploadedEvent eventRequest
) {
}
