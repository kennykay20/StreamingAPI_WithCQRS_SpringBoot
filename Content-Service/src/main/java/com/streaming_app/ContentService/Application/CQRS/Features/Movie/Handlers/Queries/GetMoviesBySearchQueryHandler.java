package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Queries;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries.GetMoviesBySearchQuery;
import com.streaming_app.ContentService.Application.Dtos.Mapper.MovieMapper;
import com.streaming_app.ContentService.Application.Dtos.Responses.MovieResponseDto;
import com.streaming_app.ContentService.Persistence.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMoviesBySearchQueryHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public List<MovieResponseDto> handle(GetMoviesBySearchQuery request)
    {
        var movies = movieRepository
                     .findByTitleContainingIgnoreCase(request.title());

        log.info("Movie by title size {}", movies.size());
        return movies.stream()
                .map(movieMapper::toDto)
                .toList();
    }
}
