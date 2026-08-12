package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries;

import com.streaming_app.ContentService.Application.Dtos.Requests.GetGenreRequestDto;
import com.streaming_app.ContentService.Domain.Enums.Genre;

public record GetMoviesByGenreQuery(
        Genre genre
) {
}
