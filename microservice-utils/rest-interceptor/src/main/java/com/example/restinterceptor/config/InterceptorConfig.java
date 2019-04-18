package com.example.restinterceptor.config;

import com.example.restinterceptor.util.NoPublicLog;
import com.example.restinterceptor.util.PublicLog;
import com.example.restinterceptor.util.PublicLog2Kafka;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class InterceptorConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean(name = "loggingKafkaTemplate")
    @ConditionalOnProperty(name = "rest-logging.enabled", havingValue = "true", matchIfMissing = true)
    public KafkaTemplate kafkaTemplate(@Value("${rest-logging.kafka.broker}") String kafkaServers) {
        return new KafkaTemplate<>(loggingKafkaProducerFactory(kafkaServers));
    }

    @Bean
    @ConditionalOnBean(name = "loggingKafkaTemplate", value = KafkaTemplate.class)
    public PublicLog publicLog(@Qualifier("loggingKafkaTemplate") KafkaTemplate kafkaTemplate) {
        return new PublicLog2Kafka(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "loggingKafkaTemplate", value = KafkaTemplate.class)
    public PublicLog publicLogNoOpt() {
        return new NoPublicLog();
    }


    @Bean("restLoggingKafkaProducerFactory")
    @ConditionalOnProperty(name = "rest-logging.enabled", havingValue = "true", matchIfMissing = true)
    public ProducerFactory<String, Object> loggingKafkaProducerFactory(@Value("${rest-logging.kafka.broker}") String kafkaServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, appName);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonSerializer");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean(name = "kafkaThreadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor(@Value("${rest-logging.kafka.threadpool.keepAliveSeconds:61}") int keepAliveSeconds) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setThreadNamePrefix("kkTaskExecutor-");
        return threadPoolTaskExecutor;
    }
}
