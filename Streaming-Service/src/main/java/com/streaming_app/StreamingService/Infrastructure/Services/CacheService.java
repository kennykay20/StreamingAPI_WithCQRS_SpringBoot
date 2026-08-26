package com.streaming_app.StreamingService.Infrastructure.Services;

import com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService implements ICacheService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void invalidateCache(String cacheKey, String id) {
        String cacheKeyData = cacheKey + id;
        redisTemplate.delete(cacheKeyData);
        log.info("Cache invalidated for id {}", id);
    }

    @Override
    public void cacheValue(
            String key,
            String value
    ) {

        redisTemplate.opsForValue().set(
                key,
                value
        );

        log.info(
                "Value cached for key: {}",
                key
        );
    }

    @Override
    public void cacheValue(
            String key,
            String value,
            long timeout,
            TimeUnit timeUnit
    ) {

        redisTemplate.opsForValue().set(
                key,
                value,
                timeout,
                timeUnit
        );

        log.info(
                "Value cached for key: {} with expiration: {} {}",
                key,
                timeout,
                timeUnit
        );
    }

    @Override
    public String getCacheValue(String key) {

        return redisTemplate
                .opsForValue()
                .get(key);
    }

    @Override
    public void delete(String key) {

        redisTemplate.delete(key);

        log.info(
                "Cache deleted for key: {}",
                key
        );
    }


}
