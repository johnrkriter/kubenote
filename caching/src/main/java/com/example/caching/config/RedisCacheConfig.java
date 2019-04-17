package com.example.caching.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableConfigurationProperties(RedisCacheProperties.class)
public class RedisCacheConfig {

    /**
     * Redis Cache Manager for centralized caching
     */
    @Bean
    @ConditionalOnProperty(name="redis.cache.enabled", havingValue = "true", matchIfMissing = true)
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory, RedisCacheProperties redisCacheProperties) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .withInitialCacheConfigurations(redisCacheProperties.getInitialCacheConfigurations())
                .build();
    }

    /**
     * Fallback cache manager that does nothing when Redis cache is disabled
     */
    @Bean(name = "redisCacheManager")
    @ConditionalOnMissingBean(RedisCacheManager.class)
    public CacheManager noOpRedisCacheManager() {
        return new NoOpCacheManager();
    }
}
