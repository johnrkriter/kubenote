package com.example.cloudconfighelper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import com.example.cloudconfighelper.model.ContextRefreshLog;
import com.example.cloudconfighelper.model.ContextRefreshStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

/**
 * This class creates and sends a log to Kafka upon Spring Cloud Bus Configuration Refresh.
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "logging.enabled.kafka", havingValue = "true", matchIfMissing = true)
public class ContextRefreshLogger {

    private static final String CONFIG_CLOUD_LOGGING = "CONFIG-CLOUD-LOGGING";
    private static final SuccessCallback<Object> cloudConfigLogSuccessCallback = o -> {
        ContextRefreshStore.clearRefreshedKeys();
        log.debug("Push log message to kafka topic {} success", CONFIG_CLOUD_LOGGING);
    };
    private static final FailureCallback cloudConfigLogFailedCallback = throwable -> {
        ContextRefreshStore.clearRefreshedKeys();
        log.warn("Push log message to kafka topic {} failed, exception : {}", CONFIG_CLOUD_LOGGING, throwable.getMessage());
    };

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    @Qualifier("loggingKafkaTemplate")
    private KafkaTemplate<String, Object> loggingKafkaTemplate;

    /**
     * Listen to Spring Cloud Bus Configuration Refresh event and send log to Kafka.
     * @param event Spring Cloud Bus Configuration Refresh event
     */
    @Order
    @EventListener(RefreshRemoteApplicationEvent.class)
    public void loggingRefreshListener(RefreshRemoteApplicationEvent event){
        sendLog(ContextRefreshLog.builder()
                .id(event.getId())
                .timestamp(formatDateTime(event.getTimestamp()))
                .service(serviceName)
                .originService(event.getOriginService())
                .destinationService(event.getDestinationService())
                .refreshedKeys(ContextRefreshStore.getRefreshKeys())
                .hostName(getHostName())
                .hostAddress(getHostAddress())
                .build()
        );
    }

    /**
     * Send log to Kafka using KafkaTemplate.
     * @param contextRefreshLog Log object
     */
    private void sendLog(ContextRefreshLog contextRefreshLog) {
        log.info("After remote refresh request. >> {}", contextRefreshLog);
        this.loggingKafkaTemplate.send(CONFIG_CLOUD_LOGGING, contextRefreshLog)
                .addCallback(cloudConfigLogSuccessCallback, cloudConfigLogFailedCallback);
    }

    /**
     * @return host name from Internet Protocol (IP) address
     */
    static String getHostName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * @return host address from Internet Protocol (IP) address
     */
    static String getHostAddress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * Convert date time from milliseconds to ISO OFFSET DATE TIME format.
     * Sample output: 2011-12-03T10:15:30+01:00
     * @param millis milliseconds (long)
     * @return ISO OFFSET DATE TIME in String format
     */
    private static String formatDateTime(long millis){
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

