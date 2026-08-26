package com.streaming_app.StreamingService.Application.Contracts.Infrastructure.Interfaces;

import java.util.concurrent.TimeUnit;

public interface ICacheService {
    void invalidateCache(String cacheKey, String id);

    void cacheValue(String key, String value);

    void cacheValue(
            String key,
            String value,
            long timeout,
            TimeUnit timeUnit
    );

    String getCacheValue(String key);

    void delete(String key);
}
