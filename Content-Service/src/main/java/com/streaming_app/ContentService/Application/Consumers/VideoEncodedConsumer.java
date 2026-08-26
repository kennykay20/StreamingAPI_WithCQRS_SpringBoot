package com.streaming_app.ContentService.Application.Consumers;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Commands.UpdateMovieHlsUrlCommandHandler;
import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands.UpdateMovieHlsUrlCommand;
import com.streaming_app.ContentService.Application.Dtos.Requests.UpdateMovieHlsUrlDto;
import com.streaming_app.ContentService.Application.Events.VideoEncodedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoEncodedConsumer {

    private final UpdateMovieHlsUrlCommandHandler updateMovieHlsUrlCommandHandler;

    @KafkaListener(
            topics = "video.encoded",
            groupId = "content-service-group",
            containerFactory =
                    "videoEncodedKafkaListenerContainerFactory"
    )

    public void consumeVideoEncodedEvent(
            VideoEncodedEvent event
    ) {
        log.info(
                "Content Service, Received VideoEncodedEvent for movieId: {}",
                event.moviePublicId()
        );

        if (!event.success()) {
            log.error(
                    "Video encoding failed for movieId: {}. Error: {}",
                    event.moviePublicId(),
                    event.errorMessage()
            );

            return;
        }

        try
        {
            var request = new UpdateMovieHlsUrlDto(
                event.moviePublicId(),
                event.hlsUrl()
            );

            var command = new UpdateMovieHlsUrlCommand(
                    request
            );

            updateMovieHlsUrlCommandHandler
                    .handle(command);

            log.info(
                    "Movie updated successfully. Status: READY, movieId: {}",
                    event.moviePublicId()
            );
        }
        catch (Exception ex)
        {
            log.error(
                    "Failed to process VideoEncodedEvent for movieId: {}",
                    event.moviePublicId(),
                    ex
            );

            throw ex;
        }

    }
}
