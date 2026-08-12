package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Commands;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands.CreateMovieCommand;
import com.streaming_app.ContentService.Application.Dtos.Mapper.MovieMapper;
import com.streaming_app.ContentService.Application.Dtos.Responses.MovieResponseDto;
import com.streaming_app.ContentService.Domain.Entities.Movie;
import com.streaming_app.ContentService.Domain.Enums.VideoStatus;
import com.streaming_app.ContentService.Persistence.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class CreateMovieCommandHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public MovieResponseDto handle(CreateMovieCommand request)
    {

        log.info("AddMovie handler");
        Movie movie = new Movie();
        movie.setTitle(request.createMovieRequest().getTitle());
        movie.setDescription(request.createMovieRequest().getDescription());
        movie.setGenre(request.createMovieRequest().getGenre());
        movie.setDirector(request.createMovieRequest().getDirector());
        movie.setCast(request.createMovieRequest().getCast());
        movie.setReleaseYear(request.createMovieRequest().getReleaseYear());
        movie.setRating(request.createMovieRequest().getRating());
        movie.setThumbnailUrl(request.createMovieRequest().getThumbnailUrl());
        movie.setDurationMinutes(request.createMovieRequest().getDurationMinutes());
        movie.setVideoStatus(VideoStatus.PENDING);
        movie.setIsDeleted(false);

        var result = movieRepository.save(movie);
        log.info("Movie added successfully with Id {}", result.getPublicId());

        return movieMapper.toDto(result);
    }
}
