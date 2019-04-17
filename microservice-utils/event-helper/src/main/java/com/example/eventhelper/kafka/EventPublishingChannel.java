package com.example.eventhelper.kafka;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EventPublishingChannel {
    String EVENT_LOG = "EventLogOutput";
    @Output(EVENT_LOG)
	MessageChannel eventPublishingChannel();
}
