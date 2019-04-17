package com.example.eventhelper.constant;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventConstant {

    public static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00" ); // Set your desired format here.

    public static final String EVENT_CODE_KEY = "EVENT_CODE";
    public static final String SUB_EVENT_CODE_KEY = "SUB_EVENT_CODE";
    public static final String EVENT_NAME_KEY = "EVENT_NAME";
    public static final String USER_ID_KEY = "USER_ID";
    public static final String USER_REF_KEY = "USER_REF";
    public static final String TIMESTAMP_KEY = "TIMESTAMP";
    public static final String MOBILE_NO_KEY = "MOBILE_NO";
    public static final String CHANNEL_KEY = "CHANNEL";
    public static final String IP_KEY = "IP";
    public static final String OS_KEY = "OS";
    public static final String UPDATED_BY_KEY = "UPDATED_BY";
    public static final String CUSTOMER_LANGUAGE_KEY = "CUSTOMER_LANGUAGE";
    public static final String CORRELATION_ID_KEY = "CORRELATION_ID";
    public static final String REQUEST_ID_KEY = "REQUEST_ID";
    public static final String STATUS_KEY = "STATUS";
    public static final String CARD_ID_KEY = "CARD_ID";
    public static final String CARD_TYPE_KEY = "CARD_TYPE";
    public static final String COUNTRY_CODE_KEY = "COUNTRY_CODE";
    public static final String REGISTERED_BRANCH_KEY = "REGISTERED_BRANCH";
    public static final String DEVICE_ID_KEY = "DEVICE_ID";
    public static final String MODEL_NO_KEY = "MODEL_NO";
    public static final String TC_VERSION_KEY = "TC_VERSION";
    public static final String APP_VERSION_KEY = "APP_VERSION";
    public static final String REGISTRATION_DATE_KEY = "REGISTRATION_DATE";
    public static final String DEVICES_KEY = "DEVICES";
    public static final String INFORMATION_1_KEY = "INFORMATION_1";
    public static final String INFORMATION_2_KEY = "INFORMATION_2";
    public static final String INFORMATION_3_KEY = "INFORMATION_3";
    public static final String INFORMATION_4_KEY = "INFORMATION_4";
    public static final String INFORMATION_5_KEY = "INFORMATION_5";
    public static final String RESPONSE_CODE_KEY = "RESPONSE_CODE";
    public static final String RESPONSE_DESC_KEY = "RESPONSE_DESC";
    public static final String EXAMPLE_CHANNEL_KEY = "EXAMPLE_CHANNEL";
    public static final String ACCESS_ID_KEY = "ACCESS_ID";


    public static final String LOGIN_ID_HEADER = "loginid";
    public static final String USER_ID_HEADER = "userid";
    public static final String CHANNEL_HEADER = "channelid";
    public static final String CUSTOMER_LANGUAGE_HEADER = "accept-language";
    public static final String CORRELATION_ID_HEADER = "correlationid";
    public static final String IP_HEADER = "x-forwarded-for";
    public static final String EXAMPLE_CHANNEL_HEADER = "example-channel";
    public static final String ACCESS_ID_HEADER = "accessid";


    public static final String TRACEID_HEADER = "X-B3-TraceId";
    public static final String SPANID_HEADER = "X-B3-SpanId";
    public static final String PARENT_SPANID_HEADER = "X-B3-ParentSpanId";

    public static final String SUCCESS_VALUE = "S";
    public static final String FAILED_VALUE = "F";

    public static final DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ofPattern(EventConstant.DATETIME_PATTERN).withZone(ZoneId.of("Asia/Bangkok"));

    public static final Map<String, String> HEADER_MAPPING;
    static {
        final Map<String, String> headerMapping = new HashMap<>();
        headerMapping.put(USER_ID_HEADER, USER_ID_KEY);
        headerMapping.put(CORRELATION_ID_HEADER, CORRELATION_ID_KEY);
        headerMapping.put(CUSTOMER_LANGUAGE_HEADER, CUSTOMER_LANGUAGE_KEY);
        headerMapping.put(IP_HEADER, IP_KEY);
        headerMapping.put(CHANNEL_HEADER, CHANNEL_KEY);
        headerMapping.put(LOGIN_ID_HEADER, UPDATED_BY_KEY);
        headerMapping.put(EXAMPLE_CHANNEL_HEADER, EXAMPLE_CHANNEL_KEY);
        headerMapping.put(ACCESS_ID_HEADER, ACCESS_ID_KEY);
        HEADER_MAPPING = Collections.unmodifiableMap(headerMapping);
    }

    private EventConstant(){}


    /**
     * Returns a String that is formatted to the required format (yyyy-MM-dd'T'HH:mm:ss.SSSZ)
     *
     * @param zonedDateTime ZonedDateTime object
     * @return Zoned date time in String
     */
    public static String formatZonedDateTime(ZonedDateTime zonedDateTime) {
        String formattedDate = EventConstant.dateTimeFormatter.format(zonedDateTime);
        log.debug("Formatted date is {}", formattedDate);
        return formattedDate;
    }

    /**
     * Returns current date / time that is formatted to the required format (yyyy-MM-dd'T'HH:mm:ss.SSSZ)
     *
     * @return String current date time in yyyy-MM-dd'T'HH:mm:ss.SSSZ format
     */
    public static String currentDateTime() {
        ZonedDateTime now = ZonedDateTime.now();
        return EventConstant.formatZonedDateTime(now);
    }

    /**
     * Formats Number into standard 2 decimal places.
     * Includes: Byte,Integer,Double,Short,Float,Long
     *
     * @param number Number
     * @return String output
     */
    public static String formatNumber(Number number) {
        return DECIMAL_FORMAT.format(number);
    }

    /**
     * Formats BigDecimal into standard 2 decimal places.
     *
     * @param number BigDecimal
     * @return String output
     */
    public static String formatNumber(BigDecimal number) {
        return DECIMAL_FORMAT.format(number);
    }
    
}


