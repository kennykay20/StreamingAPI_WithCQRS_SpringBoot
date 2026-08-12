package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries;

import java.util.UUID;

public record GetMovieByPublicIdQuery(
        UUID publicId
) {
}
