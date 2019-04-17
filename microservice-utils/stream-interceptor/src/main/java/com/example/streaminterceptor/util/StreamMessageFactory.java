package com.example.streaminterceptor.util;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;

import static com.example.streaminterceptor.config.Constants.HEADER_CORRELATION_ID;
import static com.example.streaminterceptor.config.Constants.HEADER_HTTP_HEADERS;

public final class StreamMessageFactory {
    private static String appName;

    public static void setAppName(String appName) {
        StreamMessageFactory.appName = appName;
    }

    public static <T> Message<T> getMessage(T payload, HttpHeaders httpHeaders) {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.copyHeaders(httpHeaders.entrySet().stream()
                .filter(x -> !x.getKey().equalsIgnoreCase("correlationid"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        HttpHeaders temp = new HttpHeaders();
        temp.putAll(httpHeaders);
        accessor.setHeader(HEADER_HTTP_HEADERS, temp);
        accessor.setHeader("source", appName);
        return MessageBuilder
                .withPayload(payload)
                .setHeaders(accessor)
                .build();
    }

    public static <T> Message<T> getMessage(T payload, HttpHeaders httpHeaders, Map<String, Object> customHeaders) {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.copyHeaders(httpHeaders.entrySet().stream()
                .filter(x -> !x.getKey().equalsIgnoreCase("correlationid"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        HttpHeaders temp = new HttpHeaders();
        temp.putAll(httpHeaders);
        accessor.setHeader(HEADER_HTTP_HEADERS, temp);
        accessor.setHeader(HEADER_CORRELATION_ID, httpHeaders.getFirst(HEADER_CORRELATION_ID));
        accessor.copyHeaders(customHeaders);
        accessor.setHeader("source", appName);
        return MessageBuilder
                .withPayload(payload)
                .setHeaders(accessor)
                .build();
    }

}
