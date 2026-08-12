package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Queries;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries.GetMovieByPublicIdQuery;
import com.streaming_app.ContentService.Application.Dtos.Mapper.MovieMapper;
import com.streaming_app.ContentService.Application.Dtos.Responses.MovieResponseDto;
import com.streaming_app.ContentService.Application.Exceptions.MovieNotFoundException;
import com.streaming_app.ContentService.Persistence.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class GetMovieByPublicIdQueryHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieResponseDto handle(GetMovieByPublicIdQuery request)
    {
        var movie = movieRepository
                .findByPublicId(request.publicId())
                .orElseThrow(() ->
                   new MovieNotFoundException(
                           "Movie not found"
                   )
                );

        return movieMapper.toDto(movie);
    }
}
