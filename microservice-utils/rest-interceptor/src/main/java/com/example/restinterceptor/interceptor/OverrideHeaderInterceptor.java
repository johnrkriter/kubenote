package com.example.restinterceptor.interceptor;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Map;

/**
 * This interceptor provides a convenience to override the values of a HTTP header with another.
 */
@AllArgsConstructor
public class OverrideHeaderInterceptor implements ClientHttpRequestInterceptor {

    private Map<String, String> headerMappings;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        for (Map.Entry<String, String> mapping : headerMappings.entrySet()) {
            String originalHeader = mapping.getKey();
            String overrideHeader = mapping.getValue();
            if (httpRequest.getHeaders().containsKey(originalHeader)) {
                httpRequest.getHeaders().put(originalHeader, httpRequest.getHeaders().get(overrideHeader));
            }
        }
        return execution.execute(httpRequest, body);
    }
}
