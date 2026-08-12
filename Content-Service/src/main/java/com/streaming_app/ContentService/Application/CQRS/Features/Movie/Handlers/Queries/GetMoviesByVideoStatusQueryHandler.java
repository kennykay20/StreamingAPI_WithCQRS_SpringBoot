package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Queries;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Queries.GetMoviesByVideoStatusQuery;
import com.streaming_app.ContentService.Application.Dtos.Mapper.MovieMapper;
import com.streaming_app.ContentService.Application.Dtos.Responses.MovieResponseDto;
import com.streaming_app.ContentService.Persistence.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMoviesByVideoStatusQueryHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public List<MovieResponseDto> handle(GetMoviesByVideoStatusQuery request)
    {
        var movies = movieRepository.findByVideoStatus(request.videoStatus());

        log.info("Movie by video status list - {}", movies.size());
        return movies.stream()
                .map(movieMapper::toDto)
                .collect(Collectors.toList());
    }
}
