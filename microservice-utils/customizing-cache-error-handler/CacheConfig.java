package com.example.cacheerrorhandlerdemo;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCache;

/**
 * Sample snippet on how to override default {@link CacheErrorHandler}
 */
@Slf4j
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

	@Bean
	@Override
	public CacheErrorHandler errorHandler() {
		return new CacheErrorHandler() {
			@Override
			public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
				if (cache instanceof RedisCache) {
					log.warn("Failed to retrieve RedisCache:{} object:{}", cache.getName(), o.toString(), e);
				} else {
					log.warn("Failed to retrieve unknown cache type:{}, cache name object:{}", cache.getClass(), o.toString(), e);
				}
			}

			@Override
			public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
				if (cache instanceof RedisCache) {
					log.warn("Failed to store RedisCache:{} object:{}", cache.getName(), o.toString(), e);
				} else {
					log.warn("Failed to store unknown cache type:{}, cache name object:{}", cache.getClass(), o.toString(), e);
				}

			}

			@Override
			public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
				if (cache instanceof RedisCache) {
					log.warn("Failed to evict RedisCache:{} object:{}", cache.getName(), o.toString(), e);
				} else {
					log.warn("Failed to evict unknown cache type:{}, cache name object:{}", cache.getClass(), o.toString(), e);
				}

			}

			@Override
			public void handleCacheClearError(RuntimeException e, Cache cache) {
				if (cache instanceof RedisCache) {
					log.warn("Failed to clear RedisCache:{}", cache.getName(), e);
				} else {
					log.warn("Failed to clear unknown cache type:{}", cache.getClass(), e);
				}

			}
		};
	}
}