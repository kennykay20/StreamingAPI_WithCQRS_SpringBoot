package com.streaming_app.StreamingService.Application.Consumers;

import com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces.ICacheService;
import com.streaming_app.StreamingService.Application.Events.VideoEncodedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoEncodedConsumer {

    private static final String MASTER_PLAYLIST_KEY_PREFIX
            = "streaming:playlist:";

    private static final String STREAMING_URL_CACHE_PREFIX
            = "streaming:url:";

    private final ICacheService cacheService;

    /**
     * Listens to video encoded kafka topic
     * Stores mater playlist key in Redis when encoding is complete
     * This allows StreamService to quickly find the playlist key by moviePublicId
     */
    @KafkaListener(
            topics = "video.encoded",
            groupId = "streaming-service-group"
    )

    public void consumeVideoEncodedEvent(VideoEncodedEvent event) {
        log.info(
                "Streaming Service, Received VideoEncodedEvent for movieId: {}, success: {}",
                event.moviePublicId(),
                event.success()
        );

        if (!event.success()) {
            log.error(
                    "Video encoding failed for movieId: {}. Error: {}",
                    event.moviePublicId(),
                    event.errorMessage()
            );

            return;
        }

        String movieId = event.moviePublicId().toString();

        String playlistCacheKey = MASTER_PLAYLIST_KEY_PREFIX + movieId;

        String streamingUrlCacheKey = STREAMING_URL_CACHE_PREFIX + movieId;

        /*
         *Save the latest master playlist key.
         */

        cacheService.cacheValue(
                playlistCacheKey,
                event.masterPlaylistKey()
        );

        /*
         * If the video was re-encoded, remove any previously
         * generated streaming URL.
         *
         * A new request will generate a fresh SAS URL.
         */

        cacheService.delete(
                streamingUrlCacheKey
        );

        log.info(
                "Streaming information cached successfully for movieId: {}",
                movieId
        );
    }
}
