package com.example.restinterceptor.util;

import com.example.restinterceptor.filter.Packet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
public class PublicLog2Kafka implements PublicLog {

    private final ListenableFutureCallback requestLogCallback;
    private final ListenableFutureCallback externalRequestLogCallback;
    private final ListenableFutureCallback internalRequestLogCallback;
    @Value("${rest-logging.requestLogTopic:REQUEST-LOG}")
    private String requestLogTopic;
    @Value("${rest-logging.externalRequestLogTopic:REQUEST-EXTERNAL-LOG}")
    private String externalRequestLogTopic;
    @Value("${rest-logging.internalRequestLogTopic:REQUEST-INTERNAL-LOG}")
    private String internalRequestLogTopic;
    private KafkaTemplate kafkaTemplate;

    public PublicLog2Kafka(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.requestLogCallback = new KafkaListenableFutureCallback(requestLogTopic);
        this.externalRequestLogCallback = new KafkaListenableFutureCallback(externalRequestLogTopic);
        this.internalRequestLogCallback = new KafkaListenableFutureCallback(internalRequestLogTopic);
        log.debug("Public log2 kafka create");
    }

    @Async("kafkaThreadPoolTaskExecutor")
    @Override
    public void send(Packet packet) {
        kafkaTemplate.send(requestLogTopic, packet).addCallback(requestLogCallback);
    }

    @Async("kafkaThreadPoolTaskExecutor")
    @Override
    public void sendExternal(Packet packet) {
        kafkaTemplate.send(externalRequestLogTopic, packet).addCallback(externalRequestLogCallback);
    }

    @Async("kafkaThreadPoolTaskExecutor")
    @Override
    public void sendInternal(Packet packet) {
        kafkaTemplate.send(internalRequestLogTopic, packet).addCallback(internalRequestLogCallback);
    }

    private class KafkaListenableFutureCallback implements ListenableFutureCallback {
        private String topic;

        public KafkaListenableFutureCallback(String topic) {
            this.topic = topic;
        }

        @Override
        public void onFailure(Throwable throwable) {
            log.info("Push packet log failed : Topic = {}, Error = {}", this.topic, throwable.getMessage());
        }

        @Override
        public void onSuccess(Object o) {
            log.info("Push packet log success : Topic = {}", this.topic);
        }
    }
}
