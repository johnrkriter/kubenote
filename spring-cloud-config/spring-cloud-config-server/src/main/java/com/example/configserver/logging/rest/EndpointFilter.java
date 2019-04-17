package com.example.configserver.logging.rest;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Perform request logging using Spring Cloud Stream
 */
@Component
@EnableBinding(LogSource.class)
public class EndpointFilter extends OncePerRequestFilter {

    private static final String SERVICE = "ms_service";
    private static final String HOSTNAME = "ms_hostname";
    private static final String IP = "ms_ip";
    private MessageChannel output;

    public EndpointFilter(@Autowired @Qualifier(LogSource.OUTPUT) MessageChannel output) {
        this.output = output;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            output.send(MessageBuilder.withPayload(
                    new RequestLogging(getCurrentDateTime(), httpServletRequest.getRequestURI(),
                            httpServletResponse.getStatus(), httpServletRequest.getHeader(SERVICE),
                            httpServletRequest.getHeader(HOSTNAME), httpServletRequest.getHeader(IP)))
                    .build());
        }
    }

    private String getCurrentDateTime(){
        return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
