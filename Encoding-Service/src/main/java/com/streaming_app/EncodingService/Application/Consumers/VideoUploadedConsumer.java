package com.streaming_app.EncodingService.Application.Consumers;

import com.streaming_app.EncodingService.Application.CQRS.Features.Encode.Handlers.Commands.EncodeVideoCommandHandler;
import com.streaming_app.EncodingService.Application.CQRS.Features.Encode.Requests.Commands.EncodeVideoCommand;
import com.streaming_app.EncodingService.Application.Events.VideoUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
//@RequiredArgsConstructor
public class VideoUploadedConsumer {

    private final ConsumerFactory<String, VideoUploadedEvent> consumerFactory;
    private final EncodeVideoCommandHandler encodeVideoCommandHandler;

    public VideoUploadedConsumer(
            EncodeVideoCommandHandler encodeVideoCommandHandler,
            ConsumerFactory<String, VideoUploadedEvent> consumerFactory
    ) {
        this.consumerFactory = consumerFactory;
        this.encodeVideoCommandHandler = encodeVideoCommandHandler;
    }
    /**
     * Listens to video.uploaded Kafka topic.
     * Triggered when video service uploads a raw video to Blob
     *
     * FLOW:
     *
     * Video Service -> Blob upload -> Kafka (video.uploaded)
     *                              -> This consumer
     *                              -> EncodingService -> FFmpeg -> blob
     *                              -> Kafka (video.encoded)
     */

    @KafkaListener(
            topics = "video.uploaded",
            groupId = "encoding-service-group"
    )
    public void consumeVideoUploadedEvent(VideoUploadedEvent event){

        log.info(
                "Kafka max.poll.interval.ms = {}",
                consumerFactory
                        .getConfigurationProperties()
                        .get("max.poll.interval.ms")
        );
        log.info(
                "Consumed videoUploadedEvent for movieId: {} file: {}",
                event.moviePublicId(),
                event.fileName()
        );

        var command = new EncodeVideoCommand(event);

        try
        {
            encodeVideoCommandHandler.handle(command);
        }
        catch (Exception ex)
        {
            log.error(
                    "Failed to process encoding for movieId: {} ",
                    event.moviePublicId(),
                    ex
            );

            throw ex;
        }
    }
}
