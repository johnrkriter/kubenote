package com.example.restinterceptor.util;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LogMaskUtil {
    public static final Pattern PATTERNCARD = Pattern.compile("[\"]((?:(?:4\\d{3})|(?:5[1-5]\\d{2})|6(?:011|5[0-9]{2}))(?:-?|\\040?)(?:\\d{4}(?:-?|\\040?)){3}|(?:3[4,7]\\d{2})(?:-?|\\040?)\\d{6}(?:-?|\\040?)\\d{5})[\"]");

    private LogMaskUtil() {
    }

    public static String doMask(String txt) {
        Matcher matcher = PATTERNCARD.matcher(txt);

        StringBuffer sb = new StringBuffer();
        log.debug("groupCount : {}", matcher.groupCount());
        while (matcher.find()) {
            log.debug("group 1 >> {}", matcher.group(1));
            matcher.appendReplacement(sb, '\"' + maskCreditCardNo(matcher.group(1)) + '\"');
        }
        matcher.appendTail(sb);

        if (log.isDebugEnabled()) log.debug("mask result : {}", sb.toString());
        return sb.toString();
    }


    public static String maskCreditCardNo(String creditNo) {
        int maskCount = creditNo.length() - 10;
        String mask = maskCount <= 0 ? "xxxxxxxxxx" : String.format("%" + maskCount + "s", "").replace(' ', 'x');
        log.debug("mask : {}", mask);
        String result = maskCount <= 0 ? mask : creditNo.substring(0, 6) + mask + creditNo.substring(6 + mask.length());
        log.debug("after mask : {}", result);
        return result;
    }
}
