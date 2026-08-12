package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries;

import com.streaming_app.ContentService.Domain.Enums.VideoStatus;

public record GetMoviesByVideoStatusQuery(
        VideoStatus videoStatus
) {
}
