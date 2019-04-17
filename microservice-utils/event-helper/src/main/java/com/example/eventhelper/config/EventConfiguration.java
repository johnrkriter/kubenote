package com.example.eventhelper.config;

import java.util.HashMap;

import lombok.extern.slf4j.Slf4j;
import com.example.eventhelper.kafka.EventPublishingChannel;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableBinding(EventPublishingChannel.class)
@Configuration
@PropertySource("classpath:event-helper.properties")
@EnableAsync
public class EventConfiguration {

    @Bean(name = "eventBean")
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public HashMap<String, Object> eventBean() {
        log.debug("CREATING EVENT BEAN");
        return new HashMap<>();
    }

}
