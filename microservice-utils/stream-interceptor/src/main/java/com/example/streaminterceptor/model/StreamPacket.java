package com.example.streaminterceptor.model;

import java.io.Serializable;

import com.example.streaminterceptor.config.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.messaging.MessageHeaders;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class StreamPacket implements Serializable {

    /**
     * Spring Cloud Stream
     */
    private String id;
    private String channel;
    private String service;
    private String msgBody;
    private int msgBodyLength;
    private MessageHeaders msgHeaders;
    private String contentType;
    private String msgSent;

    /**
     * Kafka
     */
    private String kafkaReceivedTopic;
    private String kafkaOffset;
    private String kafkaReceivedPartitionId;

    /**
     * Spring Cloud Sleuth
     */
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String spanSampled;

    /**
     * Others
     */
    private String serverName;
    private int serverPort;
    private String remoteServer;
    private int remotePort;
    private String localAddr;
    private String clientIp;
    private long executeTime;
    private Constants.ChannelMethod method;
    private String source;

}
