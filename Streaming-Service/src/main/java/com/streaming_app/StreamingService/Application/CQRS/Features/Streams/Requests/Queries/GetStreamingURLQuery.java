package com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Requests.Queries;

import com.streaming_app.StreamingService.Application.Dtos.Requests.StreamingUrlRequestDto;

import java.util.UUID;

public record GetStreamingURLQuery(
        StreamingUrlRequestDto requestDto
) {
}
