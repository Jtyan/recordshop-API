package com.northcoders.recordshopAPI.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class GenericCacheService {

    private final CacheManager cacheManager;

    public GenericCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public <T> T getCachedValue(String cacheName, Object key, long expirations, FetchFunction<T> fetchFunction) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalArgumentException("Cache not found: " + cacheName);
        }

        Cache.ValueWrapper wrapper = cache.get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }

        T value = fetchFunction.fetch();
        cache.put(key, value);

        return value;
    }

    public void evictCacheValue(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    @FunctionalInterface
    public interface FetchFunction<T> {
        T fetch();
    }

}
