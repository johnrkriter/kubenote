package com.example.cloudconfighelper.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@ConditionalOnMissingBean(name="loggingKafkaTemplate")
@Configuration
public class KafkaClientConfig {
    @Value("${spring.application.name}")
    private String appName;

    @Bean(name="loggingKafkaTemplate")
    @ConditionalOnProperty(name = "logging.enabled.kafka", havingValue = "true", matchIfMissing = true)
    public KafkaTemplate kafkaTemplate(@Value("${spring.kafka.logging.bootstrap-servers}") String kafkaServers) {
        return new KafkaTemplate<>(loggingKafkaProducerFactory(kafkaServers));
    }

    public ProducerFactory<String, Object> loggingKafkaProducerFactory(@Value("${spring.kafka.logging.bootstrap-servers}") String kafkaServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, appName);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
