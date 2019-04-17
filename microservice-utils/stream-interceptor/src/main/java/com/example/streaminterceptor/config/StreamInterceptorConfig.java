package com.example.streaminterceptor.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import com.example.streaminterceptor.util.PayloadToStringConverter;
import com.example.streaminterceptor.util.StreamLog2Kafka;
import com.example.streaminterceptor.util.StreamLogger;
import com.example.streaminterceptor.util.StreamLogNoOpt;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaderMapper;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@Slf4j
@PropertySource("classpath:stream-interceptor.properties")
public class StreamInterceptorConfig {

    @Value("${spring.application.name}")
    private String appName;

    @Bean(name="streamLoggingKafkaTemplate")
    @ConditionalOnProperty(name = "stream-logging.enabled", havingValue = "true", matchIfMissing = true)
    public KafkaTemplate kafkaTemplate(@Value("${stream-logging.kafka.broker}") String kafkaServers) {
        return new KafkaTemplate<>(loggingKafkaProducerFactory(kafkaServers));
    }

    @Bean
    @ConditionalOnBean(value = KafkaTemplate.class, name = "streamLoggingKafkaTemplate")
    public StreamLogger streamLog(@Qualifier("streamLoggingKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate) {
        return new StreamLog2Kafka(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(value = KafkaTemplate.class, name = "streamLoggingKafkaTemplate")
    public StreamLogger streamLogNoOpt() {
        return new StreamLogNoOpt();
    }

    @Bean(name="streamLoggingKafkaProducerFactory")
    @ConditionalOnProperty(name = "stream-logging.enabled", havingValue = "true", matchIfMissing = true)
    public ProducerFactory<String, Object> loggingKafkaProducerFactory(@Value("${stream-logging.kafka.broker}") String kafkaServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, appName);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.springframework.kafka.support.serializer.JsonSerializer");
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean(name = "streamKafkaThreadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor(@Value("${kafka.threadpool.keepAliveSeconds:61}")int keepAliveSeconds) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        threadPoolTaskExecutor.setThreadNamePrefix("streamKafkaTaskExecutor-");
        return threadPoolTaskExecutor;
    }

    @Bean(name = "customHeaderMapper")
    public KafkaHeaderMapper customHeaderMapper(){
        DefaultKafkaHeaderMapper defaultKafkaHeaderMapper = new DefaultKafkaHeaderMapper();
        defaultKafkaHeaderMapper.addTrustedPackages(
                "org.springframework.http",
                "java.util",
                "java.lang",
                "org.springframework.util"
        );
        return defaultKafkaHeaderMapper;
    }

    @Bean(name = "streamInterceptorConverter")
    public MessageConverter streamMessageConverter() {
        return new PayloadToStringConverter();
    }
}
