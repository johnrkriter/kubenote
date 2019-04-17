package com.example.eventhelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import com.example.eventhelper.constant.EventConstant;
import com.example.eventhelper.kafka.KafkaPublisher;
import com.example.streaminterceptor.util.StreamMessageFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class EventPublisher {

    @Value("#{'${event-bean.mandatory-field.comma-separate-list}'.split(',')}")
    private List<String> eventBeanMandatoryList;

    @Autowired
    private HttpHeaders httpHeaders;
    @Autowired
    private KafkaPublisher kafkaPublisher;
    @Autowired(required = false)
    @Qualifier("eventBean")
    private HashMap<String, Object> eventBean;

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * This method has execute after method with EnableEventPublishing helper is finish and return success.
     */
    @AfterReturning("@annotation(com.example.eventhelper.EnableEventPublishing)")
    public void prepareToSendMessage() {
        log.debug("-------EnableEventPublishing is running! (@AfterReturning) -------");
        sendEventLog(httpHeaders, Collections.emptyMap());
    }

    /**
     * This method has execute after method with EnableEventPublishing helper is finish and throw exception.
     * In this one set Status = F if eventBean hasn't Status
     */
    @AfterThrowing("@annotation(com.example.eventhelper.EnableEventPublishing)")
    public void prepareToSendMessageFromException() {
        log.debug("-------EnableEventPublishing is running! (@AfterThrowing) -------");
        if (!eventBean.containsKey(EventConstant.STATUS_KEY)) {
            log.debug("Setting status to eventBean as 'F' due to missing status.");
            eventBean.put(EventConstant.STATUS_KEY, EventConstant.FAILED_VALUE);
        }
        sendEventLog(httpHeaders, Collections.emptyMap());
    }

    /**
     * Do send eventLog using wired eventBean.
     * Should be used in requested scope that eventBean exist.
     * @param headers
     * @param additionalInfo
     */
    public void sendEventLog(Map<String, List<String>> headers, Map<String, Object> additionalInfo) {
        doSendEventLog(headers, eventBean, additionalInfo);
    }

    /**
     * Do send eventLog without using wired eventBean.
     * Should be used in non-request scope that eventBean is not exist.
     * @param headers
     * @param additionalInfo
     */
    public void sendEventLogWithCustomHeader(Map<String, List<String>> headers, Map<String, Object> additionalInfo) {
        doSendEventLog(headers, null, additionalInfo);
    }

    public void doSendEventLog(Map<String, List<String>> headers, Map<String, Object> eventBean, Map<String, Object>
            additionalInfo) {
        log.info("Publishing message...");
        log.debug("headers : {}", headers);
        log.debug("eventBean : {}", eventBean);
        log.debug("additionalInfo : {}", additionalInfo);
        Map<String, Object> eventBeanClone = new HashMap<>();
        if (eventBean != null) {
            eventBeanClone.putAll(eventBean);
            eventBean.clear();
        }
        putHeadersToEventBean(eventBeanClone, headers);
        putOtherDetailsInEventBean(eventBeanClone, additionalInfo);
        removeNullValueFromEventBean(eventBeanClone);
        if (isMandatoryFieldMissing(eventBeanClone)) {
            log.warn("Event is not published due to missing mandatory field.");
            return;
        }

        try {
            Message<String> message = buildMessage(eventBeanClone, headers);
            kafkaPublisher.publish(message);
        } catch (JsonProcessingException jsonEx) {
            log.error("Error occurred on publishing can't parse eventBean to json.", jsonEx);
        }
    }

    public void doSendEventLogWithMessageHeaders(Map<String, List<String>> headers, Map<String, Object> eventBean, Map<String, Object>
            additionalInfo, MessageHeaders messageHeaders) {
        log.info("Publishing message...");
        log.debug("headers : {}", headers);
        log.debug("eventBean : {}", eventBean);
        log.debug("additionalInfo : {}", additionalInfo);
        Map<String, Object> eventBeanClone = new HashMap<>();
        if (eventBean != null) {
            eventBeanClone.putAll(eventBean);
            eventBean.clear();
        }
        putHeadersToEventBean(eventBeanClone, headers);
        putOtherDetailsInEventBean(eventBeanClone, additionalInfo);
        removeNullValueFromEventBean(eventBeanClone);
        if (isMandatoryFieldMissing(eventBeanClone)) {
            log.warn("Event is not published due to missing mandatory field.");
            return;
        }

        // Remove kafka_receivedTimestamp
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.copyHeaders(messageHeaders);
        accessor.removeHeader("kafka_receivedTimestamp");

        try {
            Message<String> message = buildMessageWithMessageHeaders(eventBeanClone, headers, accessor.getMessageHeaders());
            kafkaPublisher.publish(message);
        } catch (JsonProcessingException jsonEx) {
            log.error("Error occurred on publishing can't parse eventBean to json.", jsonEx);
        }
    }

    private void putHeadersToEventBean(Map<String, Object> eventBeanClone, Map<String, List<String>> headers) {
        boolean gotUserIdInMap = eventBeanClone.containsKey(EventConstant.USER_ID_KEY);
        for (Map.Entry<String, List<String>> eachHeader : headers.entrySet()) {
            String lowerKey = eachHeader.getKey().toLowerCase();
            List<String> value = eachHeader.getValue();
            if (EventConstant.HEADER_MAPPING.containsKey(lowerKey) && !eventBeanClone.containsKey(lowerKey)) {
                if (EventConstant.USER_ID_HEADER.equals(lowerKey)) {
                    if (!gotUserIdInMap) {
                        eventBeanClone.put(EventConstant.HEADER_MAPPING.get(lowerKey), value.get(0));
                        log.debug("Putting header {}:{}", eachHeader.getKey(), value);
                    }
                } else {
                    eventBeanClone.put(EventConstant.HEADER_MAPPING.get(lowerKey), value.get(0));
                    log.debug("Putting header {}:{}", eachHeader.getKey(), value);
                }
            }
        }

        // Remove USER_ID if USER_REF exists, because NTB users only have USER_REF but not USER_ID
        if (eventBeanClone.containsKey(EventConstant.USER_REF_KEY) && !gotUserIdInMap) {
            eventBeanClone.remove(EventConstant.USER_ID_KEY);
        }
    }

    private void putOtherDetailsInEventBean(Map<String, Object> eventBeanClone, Map<String, Object> additionalInfo) {
        if (!eventBeanClone.containsKey(EventConstant.UPDATED_BY_KEY) ||
                (eventBeanClone.get(EventConstant.UPDATED_BY_KEY) == null)) {
            if(eventBeanClone.containsKey(EventConstant.USER_ID_KEY)) {
                eventBeanClone.put(EventConstant.UPDATED_BY_KEY, eventBeanClone.get(EventConstant.USER_ID_KEY));
            } else if(eventBeanClone.containsKey(EventConstant.USER_REF_KEY)) {
                eventBeanClone.put(EventConstant.UPDATED_BY_KEY, eventBeanClone.get(EventConstant.USER_REF_KEY));
            }
        }

        for (Map.Entry<String, Object> info : additionalInfo.entrySet()) {
            eventBeanClone.put(info.getKey(), info.getValue());
        }

        if (eventBeanClone.containsKey(EventConstant.CHANNEL_KEY)) {
            if ("MOB".equalsIgnoreCase(eventBeanClone.get(EventConstant.CHANNEL_KEY).toString()) ||
                    "MOBI".equalsIgnoreCase(eventBeanClone.get(EventConstant.CHANNEL_KEY).toString())) {
                eventBeanClone.replace(EventConstant.CHANNEL_KEY, "ENET");
            }
        } else {
            eventBeanClone.put(EventConstant.CHANNEL_KEY, "ENET");
        }

        if (!eventBeanClone.containsKey(EventConstant.TIMESTAMP_KEY)) {
            eventBeanClone.put(EventConstant.TIMESTAMP_KEY, EventConstant.currentDateTime());
        }
    }

    private void removeNullValueFromEventBean(Map<String, Object> eventBeanClone) {
        log.debug("before remove null value: {}", eventBeanClone);
        eventBeanClone.entrySet().removeIf(entry -> null == entry.getValue());
        log.debug("after remove null value: {}", eventBeanClone);
    }

    private boolean isMandatoryFieldMissing(Map<String, Object> eventBeanClone) {
        List<String> missingMandatoryFields =
                eventBeanMandatoryList.stream()
                        .filter(each -> !eventBeanClone.containsKey(each))
                        .collect(Collectors.toList());
        if (!missingMandatoryFields.isEmpty()) {
            log.warn("Missing mandatory field(s): {} | eventBean: {}", missingMandatoryFields, eventBeanClone);
            return true;
        }
        return false;
    }


    private Message<String> buildMessage(Map<String, Object> eventBean, Map<String, List<String>> headers)
            throws JsonProcessingException {
        log.debug("Building message..");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(headers);
        Message<String> message = StreamMessageFactory.getMessage(mapToJson(eventBean),httpHeaders);
        log.debug("Message build successfully : {}", message);
        return message;
    }

    private Message<String> buildMessageWithMessageHeaders(Map<String, Object> eventBean, Map<String, List<String>> headers, MessageHeaders messageHeaders)
            throws JsonProcessingException {
        log.debug("Building message..");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(headers);
        Message<String> message = StreamMessageFactory.getMessage(mapToJson(eventBean), httpHeaders, messageHeaders);
        log.debug("Message build successfully : {}", message);
        return message;
    }

    private String mapToJson(Map map) throws JsonProcessingException {
        String jsonString = mapper.writeValueAsString(map);
        log.debug("jsonString : {}", jsonString);
        return jsonString;
    }
}
