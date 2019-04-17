package com.example.restinterceptor.interceptor;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

/**
 * This interceptor used for remove the header from the outgoing request
 *
 * @author Created by Pongsakorn Somsri on 11/28/2018.
 */
@Slf4j
@AllArgsConstructor
public class RemoveHeaderInterceptor implements ClientHttpRequestInterceptor {

    private List<String> headersToBeRemoved;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        headersToBeRemoved.forEach(toBeRemoved -> {
            if (httpRequest.getHeaders().containsKey(toBeRemoved)) {
                log.trace("Remove {} from the httpHeaders by RemoveHeaderInterceptor", toBeRemoved);
                httpRequest.getHeaders().remove(toBeRemoved);
            }
        });
        return execution.execute(httpRequest, body);
    }
}
