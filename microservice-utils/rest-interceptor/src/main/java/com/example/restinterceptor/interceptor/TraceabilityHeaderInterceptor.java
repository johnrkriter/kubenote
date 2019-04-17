package com.example.restinterceptor.interceptor;

import com.example.restinterceptor.util.InterceptorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;

import static com.example.restinterceptor.config.InterceptorConstants.CORRELATION_ID;

/**
 * This interceptor inserts some custom HTTP headers for traceability between microservices.
 */
@Slf4j
public class TraceabilityHeaderInterceptor implements ClientHttpRequestInterceptor {

    @Value("${spring.application.name}")
    private String appName;
    private boolean generateCorrelationIdIfMissing = true;

    public void setGenerateCorrelationIdIfMissing(boolean generateCorrelationIdIfMissing) {
        this.generateCorrelationIdIfMissing = generateCorrelationIdIfMissing;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {

        // source
        request.getHeaders().set("source", appName);

        // correlationId
        if (generateCorrelationIdIfMissing && !request.getHeaders().containsKey(CORRELATION_ID)) {
            if (RequestContextHolder.getRequestAttributes() != null) {
                HttpServletRequest curRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                String correlationId = curRequest.getHeader(CORRELATION_ID);
                if (!StringUtils.isEmpty(correlationId)) {
                    log.warn("Correlation ID not found in current request header. Copying value from original request header.");
                    request.getHeaders().put(CORRELATION_ID, Collections.singletonList(correlationId));
                } else {
                    log.warn("Correlation ID not found in request header. Generating new Correlation ID.");
                    request.getHeaders().put(CORRELATION_ID, Collections.singletonList(InterceptorUtil.generateUUID36()));
                }
            } else {
                log.warn("Correlation ID not found in request header. Generating new Correlation ID.");
                request.getHeaders().put(CORRELATION_ID, Collections.singletonList(InterceptorUtil.generateUUID36()));
            }
        }

        return execution.execute(request, bytes);
    }
}
