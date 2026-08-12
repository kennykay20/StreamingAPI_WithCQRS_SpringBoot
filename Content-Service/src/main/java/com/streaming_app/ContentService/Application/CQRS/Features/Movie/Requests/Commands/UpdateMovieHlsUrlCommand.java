package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands;

import com.streaming_app.ContentService.Application.Dtos.Requests.UpdateMovieHlsUrlDto;

public record UpdateMovieHlsUrlCommand(
    UpdateMovieHlsUrlDto updateHlsRequest
) {
}
