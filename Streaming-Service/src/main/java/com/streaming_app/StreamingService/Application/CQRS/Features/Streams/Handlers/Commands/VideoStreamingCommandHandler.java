package com.streaming_app.StreamingService.Application.CQRS.Features.Streams.Handlers.Commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoStreamingCommandHandler {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String MASTER_PLAYLIST_KEY_PREFIX = "streaming:playlist:";
}
