package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries;

public record GetMoviesBySearchQuery(
        String title
) {
}
