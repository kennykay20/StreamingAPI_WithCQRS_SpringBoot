package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Commands;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands.UpdateMovieVideoKeyCommand;
import com.streaming_app.ContentService.Application.Dtos.Mapper.MovieMapper;
import com.streaming_app.ContentService.Application.Exceptions.MovieNotFoundException;
import com.streaming_app.ContentService.Domain.Enums.VideoStatus;
import com.streaming_app.ContentService.Persistence.Repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j

public class UpdateMovieVideoKeyCommandHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public void handle(UpdateMovieVideoKeyCommand request)
    {
        var videoKey = request.updateRequest().getVideoKey();
        var publicId = request.updateRequest().getPublicId();


        log.info(
                "Update movie video key: {} and status: {} for movieId: {}",
                videoKey,
                VideoStatus.UPLOADED,
                publicId
        );

        var movie = movieRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found")
                );

        movie.setIsDeleted(false);
        movie.setVideoStatus(VideoStatus.UPLOADED);
        movie.setVideoKey(videoKey);

        movieRepository.save(movie);
    }
}
