package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands;

import java.util.UUID;

public record DeleteMovieCommand(
    UUID publicId
) {
}
