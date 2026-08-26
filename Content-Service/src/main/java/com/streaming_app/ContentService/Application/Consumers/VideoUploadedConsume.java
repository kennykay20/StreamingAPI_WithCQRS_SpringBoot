package com.streaming_app.ContentService.Application.Consumers;

import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Handlers.Commands.UpdateMovieVideoKeyCommandHandler;
import com.streaming_app.ContentService.Application.CQRS.Features.Movie.Requests.Commands.UpdateMovieVideoKeyCommand;
import com.streaming_app.ContentService.Application.Dtos.Requests.UpdateMovieVideoKeyDto;
import com.streaming_app.ContentService.Application.Events.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoUploadedConsume {

    private final UpdateMovieVideoKeyCommandHandler updateMovieVideoKeyCommandHandler;

    @KafkaListener(
            topics = "video.uploaded",
            groupId = "content-service-group",
            properties = {
                    "spring.json.value.default.type=com.streaming_app.ContentService.Application.Events.VideoUploadedEvent"
            }
    )
    public void consumeVideoUploadedEvent(
            VideoUploadedEvent event
    ) {

        log.info(
                "ContentService received VideoUploadedEvent for movieId: {}",
                event.moviePublicId()
        );

        try
        {
           var request = new UpdateMovieVideoKeyDto(
                event.moviePublicId(),
                event.videoKey()
           );

           var command = new UpdateMovieVideoKeyCommand(
                   request
           );

           updateMovieVideoKeyCommandHandler
                   .handle(command);

            log.info(
                    "Movie updated successfully. Status: UPLOADED, movieId: {}",
                    event.moviePublicId()
            );
        }
        catch (Exception ex)
        {
            log.error(
                    "Failed to process VideoUploadedEvent for movieId: {}",
                    event.moviePublicId(),
                    ex
            );

            throw ex;
        }
    }
}
