package com.example.caching.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration
@ConfigurationProperties(prefix="redis.cache")
@Getter
@Setter
public class RedisCacheProperties {

    private boolean enabled = true;

    private Map<String, Long> expire = new HashMap<>();

    public Map<String, RedisCacheConfiguration> getInitialCacheConfigurations() {
        Map<String, RedisCacheConfiguration> initialCacheConfigurations = new HashMap<>();
        for (Map.Entry<String, Long> entry : expire.entrySet()) {
            initialCacheConfigurations.put(entry.getKey(), RedisCacheConfiguration
					.defaultCacheConfig().entryTtl(Duration.ofSeconds(entry.getValue())));
        }
        return initialCacheConfigurations;
    }
}
