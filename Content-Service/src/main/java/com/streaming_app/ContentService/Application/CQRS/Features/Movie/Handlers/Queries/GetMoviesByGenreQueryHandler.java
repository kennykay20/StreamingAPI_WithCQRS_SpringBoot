package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Queries;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries.GetMoviesByGenreQuery;
import com.streaming_app.ContentService.Application.Dtos.Mapper.MovieMapper;
import com.streaming_app.ContentService.Application.Dtos.Responses.MovieResponseDto;
import com.streaming_app.ContentService.Persistence.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMoviesByGenreQueryHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public List<MovieResponseDto> handle(GetMoviesByGenreQuery request)
    {
        var movies = movieRepository.findByGenre(request.genre());

        return movies.stream()
                .map(movieMapper::toDto)
                .toList();
    }

}
