package com.example.streaminterceptor.util;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConverter;

/**
 * To convert incoming message from byte array to String due to Spring Cloud 2.0 changes below.
 * Read: https://docs.spring.io/spring-cloud-stream/docs/Elmhurst.RELEASE/reference/htmlsingle/#spring-cloud-stream-preface-content-type-negotiation-improvements
 */
@Slf4j
public class PayloadToStringConverter implements MessageConverter {
    @Override
    public Message<?> toMessage(Object o, MessageHeaders messageHeaders) {
        return null;
    }

    @Override
    public Object fromMessage(Message<?> message, Class<?> aClass) {
        if( aClass != String.class) {
            log.error("must cast to String, retuning null");
            return null;
        }
        byte[] payload = (byte[]) message.getPayload();
        return new String(payload);
    }
}
