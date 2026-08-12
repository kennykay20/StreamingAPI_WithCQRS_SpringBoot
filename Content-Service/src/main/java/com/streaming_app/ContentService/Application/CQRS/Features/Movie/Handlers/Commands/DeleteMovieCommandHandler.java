package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Commands;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands.DeleteMovieCommand;
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
public class DeleteMovieCommandHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    private void handle(DeleteMovieCommand request) {

        var publicId = request.publicId();


        log.info("Delete movie by publicId");
        var movie = movieRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found")
                );

        movie.setIsDeleted(true);
        movie.setVideoStatus(VideoStatus.REMOVED);

        movieRepository.save(movie);

        log.info("Movie remove ");
    }
}
