package com.example.streaminterceptor.util;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StreamMessageFactoryInitializer {
    @Value("${spring.application.name}")
    private String appName;

    @PostConstruct
    public void init() {
        StreamMessageFactory.setAppName(appName);
    }
}
