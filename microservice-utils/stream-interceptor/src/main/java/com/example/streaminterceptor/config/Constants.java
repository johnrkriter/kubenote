package com.example.streaminterceptor.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.MediaType;

public class Constants {

    /**
     * Header name
     */
    public static final String HEADER_CONTENT_TYPE = "contentType";
    public static final String HEADER_CORRELATION_ID = "correlationid";
    public static final String HEADER_HTTP_HEADERS = "httpHeaders";
    public static final String HEADER_KAFKA_RECEIVED_TOPIC = "kafka_receivedTopic";
    public static final String HEADER_KAFKA_OFFSET = "kafka_offset";
    public static final String HEADER_KAFKA_RECEIVED_PARTITION_ID = "kafka_receivedPartitionId";
    public static final String HEADER_SPAN_ID = "spanId";
    public static final String HEADER_TRACE_ID = "spanTraceId";
    public static final String HEADER_PARENT_SPAN_ID = "spanParentSpanId";
    public static final String HEADER_SPAN_SAMPLED = "spanSampled";
    public static final String HEADER_KAFKA_CONSUMER = "kafka_consumer";
    public static final String HEADER_KAFKA_RECEIVED_TIME = "kafka_receivedTimestamp";

    /**
     * Headers to be ignored during propagation
     */
    public static final Set<String> IGNORE_HEADERS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    HEADER_CONTENT_TYPE, HEADER_HTTP_HEADERS, HEADER_KAFKA_CONSUMER
            ))
    );

    /**
     * Allowed content types
     */
    public static final Set<String> ALLOWED_CONTENT_TYPE = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.TEXT_PLAIN_VALUE,
                    MediaType.APPLICATION_JSON_UTF8_VALUE,
                    MediaType.APPLICATION_JSON_UTF8_VALUE.toLowerCase()
            ))
    );

    public enum ChannelMethod {
        IN,OUT
    }

    private Constants() {
        throw new IllegalAccessError();
    }
}
