package com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Commands;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands.UpdateMovieHlsUrlCommand;
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
public class UpdateMovieHlsUrlCommandHandler {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public void handle(UpdateMovieHlsUrlCommand request)
    {
        var hlsUrl = request.updateHlsRequest().getHlsUrl();
        var publicId = request.updateHlsRequest().getPublicId();


        log.info("Update movie HlsUrl and status");
        var movie = movieRepository.findByPublicId(publicId)
                .orElseThrow(() ->
                        new MovieNotFoundException("Movie not found")
                );

        movie.setIsDeleted(false);
        movie.setVideoStatus(VideoStatus.READY);
        movie.setHlsUrl(hlsUrl);

        movieRepository.save(movie);

        log.info("Movie is ready for streaming, publicId - {}", publicId);
    }
}
