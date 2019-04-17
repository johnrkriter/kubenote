package com.example.streaminterceptor.util;

import com.example.streaminterceptor.model.StreamPacket;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class StreamLog2Kafka implements StreamLogger {

    private static final String LOG_TOPIC = "STREAM-LOG";
    private final ListenableFutureCallback requestLogCallback;

    private KafkaTemplate<String, Object> kafkaTemplate;

    public StreamLog2Kafka(KafkaTemplate<String, Object> kafkaTemplate) {
        log.info("--- Create bean StreamLog2Kafka ---");
        this.kafkaTemplate = kafkaTemplate;
        this.requestLogCallback = new ListenableFutureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.warn("Stream packet log failed: Topic = {}, Error = {}", LOG_TOPIC, throwable.getMessage());
            }

            @Override
            public void onSuccess(Object o) {
                log.debug("Stream packet log success : Topic = {}", LOG_TOPIC);
            }
        };
    }

    @Async("streamKafkaThreadPoolTaskExecutor")
    @Override
    public void send(StreamPacket streamPacket) {
        kafkaTemplate.send(LOG_TOPIC, streamPacket).addCallback(requestLogCallback);
    }

}
