package com.example.streaminterceptor;

import java.util.Arrays;

import com.example.streaminterceptor.model.StreamPacket;
import com.example.streaminterceptor.model.StreamPacketBuilder;
import com.example.streaminterceptor.util.StreamLogger;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static com.example.streaminterceptor.config.Constants.ALLOWED_CONTENT_TYPE;
import static com.example.streaminterceptor.config.Constants.HEADER_CONTENT_TYPE;

/**
 * A class to intercept stream messages for logging purposes.
 *
 * TODO: Add @RefreshScope to enable hot refresh for the channel include/exclude list
 */
@Component
@Slf4j
@GlobalChannelInterceptor
public class LoggingStreamInterceptor implements ChannelInterceptor {

    @Autowired
    private StreamLogger streamLogger;
    @Autowired
    private StreamPacketBuilder streamPacketBuilder;
    @Autowired
    @Qualifier("streamInterceptorConverter")
    private MessageConverter messageConverter;

    @Value("${stream-logging.channel.includes:#{null}}")
    private String[] includeChannels;
    @Value("${stream-logging.channel.excludes:#{null}}")
    private String[] excludeChannels;

    @Override
    public void afterSendCompletion(Message<?> msg, MessageChannel mc, boolean sent, Exception ex) {
        if(shouldIntercept(mc.toString())) {
            StreamPacket streamPacket;
            if (msg != null) {
                String stringPayload = getPayload(msg);
                if (ex != null) {
                    if (log.isDebugEnabled()) {
                        log.error("{} | channel: {} | sent: {} | message header: {}, message payload: {}",
                                ex.getMessage(), mc.toString(), sent, msg.getHeaders(), stringPayload, ex);
                    }
                } else {
                    log.debug("Intercepting Stream Message at afterSendCompletion. channel: {} | sent: {} | message header: {}, message payload: {}",
                            mc.toString(), sent ? "TRUE" : "FALSE", msg.getHeaders(), stringPayload);
                }
                streamPacket = streamPacketBuilder.preparePacket(msg, mc.toString(), sent, stringPayload);
            } else {
                if (ex != null) {
                    if (log.isDebugEnabled()) {
                        log.error("{} | channel: {} | sent: {} | message is null", ex.getMessage(), mc
                                .toString(), sent, ex);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.warn("Intercepting Stream Message at afterSendCompletion. channel: {} | sent: {} | message is null",
                                mc.toString(), sent ? "TRUE" : "FALSE");
                    }
                }
                streamPacket = streamPacketBuilder.preparePacket(mc.toString(), sent);
            }

            if (streamPacket != null) {
                streamLogger.send(streamPacket);
            }
        }
    }

    /**
     * Determine if the stream message should be intercepted
     */
    private boolean shouldIntercept(String channel){
        boolean includeAll = includeChannels == null || includeChannels.length == 0;
        boolean excludeNone = excludeChannels == null || excludeChannels.length == 0;
        if(includeAll){
            return (excludeNone || Arrays.stream(excludeChannels).noneMatch(e -> e.equalsIgnoreCase(channel)));
        }else{
            return Arrays.stream(includeChannels).anyMatch(i -> i.equalsIgnoreCase(channel))
                    && (excludeNone || Arrays.stream(excludeChannels).noneMatch(e -> e.equalsIgnoreCase(channel)));
        }
    }

    /**
     * NOTE: Message is deserialized as {@link String} when {@link org.apache.kafka.common.serialization.StringDeserializer} is set in microservice properties;
     *       otherwise message is consumed as {@link Byte[]}.
     */
    private String getPayload(Message<?> msg){
        try {
            if (msg.getHeaders().containsKey(HEADER_CONTENT_TYPE)
                    && ALLOWED_CONTENT_TYPE.contains(msg.getHeaders().get(HEADER_CONTENT_TYPE).toString().toLowerCase())) {
                if (msg.getPayload().getClass() == String.class) {
                    return msg.getPayload().toString();
                } else {
                    return messageConverter.fromMessage(msg, String.class).toString();
                }
            }
            log.debug("Ignore payload because header doesn't contain key contentType or contentType is not allowed");
            return "";
        } catch (Exception ex){
            if (log.isDebugEnabled()) {
                log.error("Unexpected exception happened.", ex);
            }
            return "";
        }
    }
}
