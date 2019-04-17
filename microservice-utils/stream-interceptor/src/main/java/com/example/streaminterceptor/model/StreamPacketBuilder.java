package com.example.streaminterceptor.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.streaminterceptor.config.Constants;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import static com.example.streaminterceptor.config.Constants.ALLOWED_CONTENT_TYPE;
import static com.example.streaminterceptor.config.Constants.HEADER_CONTENT_TYPE;
import static com.example.streaminterceptor.config.Constants.HEADER_KAFKA_OFFSET;
import static com.example.streaminterceptor.config.Constants.HEADER_KAFKA_RECEIVED_PARTITION_ID;
import static com.example.streaminterceptor.config.Constants.HEADER_KAFKA_RECEIVED_TIME;
import static com.example.streaminterceptor.config.Constants.HEADER_KAFKA_RECEIVED_TOPIC;
import static com.example.streaminterceptor.config.Constants.HEADER_PARENT_SPAN_ID;
import static com.example.streaminterceptor.config.Constants.HEADER_SPAN_ID;
import static com.example.streaminterceptor.config.Constants.HEADER_SPAN_SAMPLED;
import static com.example.streaminterceptor.config.Constants.HEADER_TRACE_ID;
import static com.example.streaminterceptor.config.Constants.IGNORE_HEADERS;

@Slf4j
@Component
public class StreamPacketBuilder {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${stream-logging.message-payload.enabled:true}")
    private boolean messagePayloadLoggingEnabled;

    // If messagePayloadSizeLimit is 0, it means unlimited
    @Value("${stream-logging.message-payload.sizelimit:24576}")
    private int messagePayloadSizeLimit;

    public StreamPacket preparePacket(Message<?> msg, String channel, boolean sent, String strPayload) {
        try {
            if (msg.getHeaders().containsKey(HEADER_CONTENT_TYPE) && ALLOWED_CONTENT_TYPE.contains(msg.getHeaders().get(HEADER_CONTENT_TYPE).toString().toLowerCase())) {
                // Source, Service, UUID
                StreamPacket streamPacket = new StreamPacket();
                streamPacket.setService(appName);
                streamPacket.setChannel(channel);
                streamPacket.setId(msg.getHeaders().containsKey("id") ? msg.getHeaders().get("id").toString() : null);

                // Message Headers, Body, Sent Time
                Map<String, Object> mapFromSet = msg.getHeaders().entrySet().stream()
                        .filter(x ->  x.getKey() != null && x.getValue() != null && !IGNORE_HEADERS.contains(x.getKey()) )
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                streamPacket.setMsgHeaders(new MessageHeaders(mapFromSet));
                streamPacket.setContentType(msg.getHeaders().get(HEADER_CONTENT_TYPE).toString());
                streamPacket.setMsgSent(sent ? "1" : "0");

                // Message Payload
                if (messagePayloadLoggingEnabled && (messagePayloadSizeLimit == 0 || messagePayloadSizeLimit >= strPayload.length())) {
                    streamPacket.setMsgBody(strPayload);
                    streamPacket.setMsgBodyLength(strPayload.length());
                } else {
                    streamPacket.setMsgBody(null);
                    streamPacket.setMsgBodyLength(0);
                }

                // Kafka
                streamPacket.setKafkaReceivedTopic(msg.getHeaders().containsKey(HEADER_KAFKA_RECEIVED_TOPIC) ? msg.getHeaders().get(HEADER_KAFKA_RECEIVED_TOPIC).toString() : null);
                streamPacket.setKafkaOffset(msg.getHeaders().containsKey(HEADER_KAFKA_OFFSET) ? msg.getHeaders().get(HEADER_KAFKA_OFFSET).toString() : null);
                streamPacket.setKafkaReceivedPartitionId(msg.getHeaders().containsKey(HEADER_KAFKA_RECEIVED_PARTITION_ID) ? msg.getHeaders().get(HEADER_KAFKA_RECEIVED_PARTITION_ID).toString() : null);

                // Spring Cloud Sleuth
                streamPacket.setSpanId(msg.getHeaders().containsKey(HEADER_SPAN_ID) ? msg.getHeaders().get(HEADER_SPAN_ID).toString() : null);
                streamPacket.setTraceId(msg.getHeaders().containsKey(HEADER_TRACE_ID) ? msg.getHeaders().get(HEADER_TRACE_ID).toString() : null);
                streamPacket.setParentSpanId(msg.getHeaders().containsKey(HEADER_PARENT_SPAN_ID) ? msg.getHeaders().get(HEADER_PARENT_SPAN_ID).toString() : null);
                streamPacket.setSpanSampled(msg.getHeaders().containsKey(HEADER_SPAN_SAMPLED) ? msg.getHeaders().get(HEADER_SPAN_SAMPLED).toString() : null);

                // Host Name and Address
                streamPacket.setServerName(getHostName());
                streamPacket.setLocalAddr(getHostAddress());

                if(msg.getHeaders().containsKey(HEADER_KAFKA_RECEIVED_TIME)){
                    streamPacket.setMethod(Constants.ChannelMethod.IN);
                    streamPacket.setExecuteTime(System.currentTimeMillis() - (long)msg.getHeaders().get("kafka_receivedTimestamp"));
                }else{
                    streamPacket.setMethod(Constants.ChannelMethod.OUT);
                    streamPacket.setExecuteTime(System.currentTimeMillis() - msg.getHeaders().getTimestamp());
                }

                if(msg.getHeaders().containsKey("source")){
                    streamPacket.setSource(msg.getHeaders().get("source", String.class));
                }

                return streamPacket;
            } else {
                log.warn("The message content-type is not is not allowed. Message content-type: {}", msg.getHeaders().get(MessageHeaders.CONTENT_TYPE));
                return null;
            }
        } catch (Exception ex) {
            log.warn("Unexpected exception occurred when preparing stream packet.", ex);
            return null;
        }
    }

    public StreamPacket preparePacket(String channel, boolean sent) {
        try {
            // Source, Service, UUID
            StreamPacket streamPacket = new StreamPacket();
            streamPacket.setService(appName);
            streamPacket.setChannel(channel);

            streamPacket.setMsgSent(sent ? "1" : "0");

            // Message Payload
            streamPacket.setMsgBodyLength(0);

            // Host Name and Address
            streamPacket.setServerName(getHostName());
            streamPacket.setLocalAddr(getHostAddress());

            if(channel.endsWith("In") || channel.endsWith("Input")){
                streamPacket.setMethod(Constants.ChannelMethod.IN);
            }else{
                streamPacket.setMethod(Constants.ChannelMethod.OUT);
            }

            return streamPacket;
        } catch (Exception ex) {
            log.warn("Unexpected exception occurred when preparing stream packet.", ex);
            return null;
        }
    }

    /**
     * @return host name from Internet Protocol (IP) address
     */
    private static String getHostName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * @return host address from Internet Protocol (IP) address
     */
    private static String getHostAddress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}

