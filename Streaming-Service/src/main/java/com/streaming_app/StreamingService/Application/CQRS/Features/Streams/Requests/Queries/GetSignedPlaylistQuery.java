package com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Requests.Queries;

import com.streaming_app.StreamingService.Application.Dtos.Requests.SignedPlaylistRequestDto;

public record GetSignedPlaylistQuery(
    SignedPlaylistRequestDto requestDto
) {
}
