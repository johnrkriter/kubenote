package com.example.eventhelper.kafka;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPublisher {

    @Autowired
    private EventPublishingChannel eventPublishingChannel;

    @Async
    public void publish(Message<String> message) {
        log.debug("Publishing audit log to kafka");
        boolean isSendSuccess = eventPublishingChannel.eventPublishingChannel().send(message);
        log.info("Audit log publication: {}.", isSendSuccess ? "success" : "failed");
    }
}
