package com.example.restinterceptor.interceptor.external;

import brave.propagation.CurrentTraceContext;
import com.example.restinterceptor.config.BufferingClientHttpResponseWrapper;
import com.example.restinterceptor.filter.Packet;
import com.example.restinterceptor.util.InterceptorUtil;
import com.example.restinterceptor.util.LogMaskUtil;
import com.example.restinterceptor.util.PublicLog;
import com.example.restinterceptor.util.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.example.restinterceptor.config.InterceptorConstants.*;

/**
 * This interceptor captures all HTTP headers of the outbound call, then send it to Kafka for logging purposes.
 * For synchronous call.
 */
@Component
@Slf4j
public class ExternalApiRequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${logging.enabled.response-body:false}")
    private boolean enabledResponseBodyLogging;
    @Value("${logging.response-body.limitsize:0}")
    private int logLimitSize;
    @Value("${logging.enabled.request-body:true}")
    private boolean enabledRequestBodyLogging;
    @Value("${logging.request-body.limitsize:0}")
    private int requestLogLimitSize;

    @Autowired
    private PublicLog publicLog;
    @Autowired
    private CurrentTraceContext currentTraceContext;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Map<String, String> reqHeaders = constructReqHeadersForLogging(httpRequest);
        long startTime = System.currentTimeMillis();
        ClientHttpResponse response = null;
        try {
            response = execution.execute(httpRequest, body);
        } finally {
            response = new BufferingClientHttpResponseWrapper(response);
            preparePacket(startTime, httpRequest, reqHeaders, body, response);
        }
        return response;
    }

    private Map<String, String> constructReqHeadersForLogging(HttpRequest request) {
        Map<String, String> reqHeaders = new HashMap<>();
        reqHeaders.put(SOURCE, serviceName);
        request.getHeaders().forEach((k, v) -> reqHeaders.put(k, String.join(",", v)));

        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest curRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            Optional.ofNullable(curRequest.getHeader(CORRELATION_ID)).filter(s -> !s.isEmpty()).ifPresent(value -> reqHeaders.put(CORRELATION_ID, value));
            Optional.ofNullable(curRequest.getHeader(API_AUTH)).filter(s -> !s.isEmpty()).ifPresent(value -> reqHeaders.put(API_AUTH, value));
            Optional.ofNullable(curRequest.getHeader(API_SCOPE)).filter(s -> !s.isEmpty()).ifPresent(value -> reqHeaders.put(API_SCOPE, value));
            Optional.ofNullable(curRequest.getHeader(USER_ID)).filter(s -> !s.isEmpty()).ifPresent(value -> reqHeaders.put(USER_ID, value));
        } else {
            log.debug("Request scope is not present, unable to copy HTTP headers.");
        }

        return reqHeaders;
    }

    private void preparePacket(long startTime, HttpRequest request, Map reqHeaders, byte[] reqBody, ClientHttpResponse response) {
        try {
            long execTime = System.currentTimeMillis() - startTime;
            Packet packet = new Packet(reqHeaders.get(CORRELATION_ID) + "-" + startTime);
            packet.setTranTime(new Date(startTime));
            packet.setExecuteTime(execTime);
            packet.setServerName(ServerInfo.getInstance().getHostName());
            packet.setLocalAddr(ServerInfo.getInstance().getHostServer());
            packet.setServerPort(request.getURI().getPort());
            packet.setReqMethod(request.getMethod().name());
            packet.setReqParams(request.getURI().getQuery());
            if (reqBody != null) {
                packet.setReqContentLength(reqBody.length);
                if (enabledRequestBodyLogging && reqBody.length > 0 && (requestLogLimitSize == 0 || reqBody.length <= requestLogLimitSize)) {
                    packet.setReqBody(LogMaskUtil.doMask(new String(reqBody, UTF_8)));
                }
            }

            packet.setReqHeaders(reqHeaders);
            packet.setUri(request.getURI().getPath());
            packet.setRawUri(InterceptorUtil.getPath(request.getURI()));
            packet.setUrl(request.getURI().toString());
            this.saveResponseToPacket(packet, response);

            // TODO: Need to be tested
            if (currentTraceContext.get() != null) {
                packet.setTraceId(String.format("%16s", Long.toHexString(currentTraceContext.get().traceId()).toLowerCase()).replace(' ', '0'));
                packet.setSpanId(String.format("%16s", Long.toHexString(currentTraceContext.get().spanId()).toLowerCase()).replace(' ', '0'));
            }
            packet.setService(EXTERNAL);

            publicLog.sendExternal(packet);

            log.trace("Overall time = {} ", System.currentTimeMillis() - startTime);
        } catch (Exception ex) {
            log.warn("Packet External Logging failed.", ex);
        }
    }

    private void saveResponseToPacket(Packet packet, ClientHttpResponse response) {
        try {
            packet.setRespHttpStatus(response.getRawStatusCode());
            packet.setRespHeaders(response.getHeaders().toSingleValueMap());
            if (enabledResponseBodyLogging) {
                byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
                packet.setRespContentLength(responseBody.length);
                if (logLimitSize == 0 || responseBody.length <= logLimitSize) {
                    packet.setRespBody(LogMaskUtil.doMask(new String(responseBody, UTF_8)));
                } else {
                    log.debug("Response body has length {} and greater than limit size {}", packet.getRespContentLength(), logLimitSize);
                }
            }
        } catch (Exception ex) {
            log.warn("Unable to log response.", ex);
        }
    }

}
