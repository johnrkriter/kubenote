package com.example.httpheaderhelper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Configuration
@Slf4j
public class HttpHeaderConfig {

    private static final String DEFAULT_LANGUAGE = "th";
    private static final String ACCEPT_LANG_KEY = HttpHeaders.ACCEPT_LANGUAGE.toLowerCase();
    private static final List<String> ALLOW_HEADER_LIST = Collections.unmodifiableList(Arrays.asList(
            ACCEPT_LANG_KEY, "x-forwarded-for",
            "requestuid", "resourceownerid", "correlationid",
            "userid", "loginid", "channelid"
    ));

    @Bean
    @RequestScope
    public HttpHeaders httpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpServletRequest curRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        Enumeration<String> headerNames = curRequest.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                String value = curRequest.getHeader(header);
                if (ALLOW_HEADER_LIST.contains(header.toLowerCase())) {
                    log.debug("Adding header {} with value {}", header, value);
                    httpHeaders.add(header, value);
                } else {
                    log.debug("Header {} with value {} is not required to be copied", header, value);
                }
            }
        }

        if (!httpHeaders.containsKey(ACCEPT_LANG_KEY) || StringUtils.isEmpty(httpHeaders.getFirst(ACCEPT_LANG_KEY))) {
            httpHeaders.set(ACCEPT_LANG_KEY, DEFAULT_LANGUAGE);
        }

        return httpHeaders;
    }
}
