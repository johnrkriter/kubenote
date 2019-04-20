package com.example.restinterceptor.filter;

import brave.Tracer;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ExtraFieldPropagation;
import com.example.restinterceptor.config.InterceptorConstants;
import com.example.restinterceptor.util.LogMaskUtil;
import com.example.restinterceptor.util.PublicLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.restinterceptor.config.InterceptorConstants.CORRELATION_ID;

@Slf4j
@Component
public class PacketRequestFilter implements Filter {

    private static final Set<String> IGNORE_URLS = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList("/hystrix.stream", "/actuator", "/auditevents", "/autoconfig", "/beans", "/configprops", "/dump", "/env", "/flyway", "/health", "/info", "/loggers", "/liquibase", "/metrics", "/mappings", "/shutdown", "/trace", "/docs", "/heapdump", "/jolokia", "/logfile"))
    );
    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${rest-logging.enabled.response-body:false}")
    private boolean enabledResponseBodyLogging;
    @Value("${rest-logging.enabled.request-body:true}")
    private boolean enabledRequestBodyLogging;
    @Value("${rest-logging.request-body.limitsize:24576}")
    private int requestLogLimitSize;
    @Value("${rest-logging.response-body.limitsize:0}")
    private int logLimitSize;
    @Value("${rest-logging.response-body.maxlimit:1048576}")
    private int maxLimit;
    @Autowired
    private PublicLog publicLog;
    @Autowired
    private CurrentTraceContext currentTraceContext;
    @Autowired
    private Tracer tracer;

    @Override
    public void init(FilterConfig filterConfig) {

    }

    private boolean shouldNotFilter(HttpServletRequest httpServletRequest) {
        return IGNORE_URLS.contains(httpServletRequest.getServletPath());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

            if (shouldNotFilter(httpServletRequest)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                doFilterInternal(filterChain, httpServletRequest, httpServletResponse);
            }
        } else {
            throw new ServletException("PacketRequestFilter only just supports HTTP requests");
        }
    }

    private void doFilterInternal(FilterChain filterChain, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(httpServletRequest);
        ContentCachingResponseWrapper response = new ContentCachingResponseWrapper(httpServletResponse);
        /*String correlationId = request.getHeader(CORRELATION_ID);
        if (correlationId != null) {
            MDC.put(InterceptorConstants.CORRELATION_ID, correlationId);
            // Propagate Correlation ID via Sleuth trace context
            try (Tracer.SpanInScope ws = tracer.withSpanInScope(tracer.nextSpan().start())) {
                ExtraFieldPropagation.set(InterceptorConstants.CORRELATION_ID, correlationId);
            }
        }*/
        try {
            filterChain.doFilter(request, response);
        } finally {
            preparePacket(startTime, request, response);
//            MDC.remove(InterceptorConstants.CORRELATION_ID);
            response.copyBodyToResponse();
        }
    }

    private void preparePacket(long startTime, ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        try {
            long execTime = System.currentTimeMillis() - startTime;

            Map<String, String> reqHeaders = getHeaders(request);

            Packet packet = new Packet(reqHeaders.get(CORRELATION_ID.toLowerCase()) + "-" + startTime);
            packet.setTranTime(new Date(startTime));
            packet.setExecuteTime(execTime);
            packet.setServerName(request.getServerName());
            packet.setServerPort(request.getServerPort());
            packet.setRemoteServer(request.getRemoteAddr());
            packet.setRemotePort(request.getRemotePort());
            packet.setContextPath(request.getContextPath());
            packet.setServletPath(request.getServletPath());
            packet.setScheme(request.getScheme());
            packet.setLocalAddr(request.getLocalAddr());
            packet.setReqMethod(request.getMethod());
            packet.setReqParams(request.getParameterMap().entrySet()
                .stream()
                .map(entry -> entry.getKey() + " - " + Arrays.toString(entry.getValue()))
                .collect(Collectors.joining(", ")));
            packet.setReqQueryParams(request.getQueryString());
            packet.setReqContentLength(request.getContentLength());
            packet.setReqBody(readRequestBody(request));
            packet.setReqHeaders(reqHeaders);
            packet.setClientIp(getClientIp(reqHeaders, request.getRemoteAddr()));
            packet.setUri(request.getRequestURI());
            packet.setRespHttpStatus(response.getStatus());

            if (enabledResponseBodyLogging && response.getContentType() != null && response.getContentType().contains(InterceptorConstants.JSON_CONTENT_TYPE)) {
                try {
                    packet.setRespContentLength(response.getContentSize());
                    if (logLimitSize == 0 || packet.getRespContentLength() <= logLimitSize) {
                        packet.setRespBody(LogMaskUtil.doMask(new String(response.getContentAsByteArray(), InterceptorConstants.UTF_8)));
                    } else {
                        log.debug("Response body has length {} and greater than limit size {}", packet.getRespContentLength(), logLimitSize);
                    }
                    if (packet.getRespContentLength() <= maxLimit) {
                        if (packet.getRespBody() == null)
                            packet.setRespStatus(readBody(response.getStatus(), response.getContentAsByteArray()));
                        else packet.setRespStatus(readBody(response.getStatus(), packet.getRespBody()));
                    }
                } catch (Exception e) {
                    log.warn("In process logging response : {}", e.getMessage());
                }
            }

            packet.setRespHeaders(getHeaders(response));
            // TODO: Need to be tested
            packet.setTraceId(String.format("%16s", Long.toHexString(currentTraceContext.get().traceId()).toLowerCase()).replace(' ', '0'));
            packet.setSpanId(String.format("%16s", Long.toHexString(currentTraceContext.get().spanId()).toLowerCase()).replace(' ', '0'));
            if (currentTraceContext.get().parentId() != null) {
                packet.setParentSpanId(String.format("%16s", Long.toHexString(currentTraceContext.get().parentId()).toLowerCase()).replace(' ', '0'));
            }
            packet.setService(serviceName);
            if (request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE) != null)
                packet.setRawUri(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString());

            Optional source = Optional.ofNullable(request.getHeader(InterceptorConstants.SOURCE));
            if ((!source.isPresent()) || source.get().equals("zuul-server")) {
                publicLog.send(packet);
            } else {
                publicLog.sendInternal(packet);
            }

            log.trace("Overall time = {} ", System.currentTimeMillis() - startTime);
        } catch (Exception ex) {
            log.warn("Packet Logging failed.", ex);
        }
    }

    private String readRequestBody(ContentCachingRequestWrapper request) {
        if (HttpMethod.GET.matches(request.getMethod())) return null;
        if (enabledRequestBodyLogging && request.getContentLength() > 0 && (requestLogLimitSize == 0 || request.getContentLength() <= requestLogLimitSize)) {
            return LogMaskUtil.doMask(new String(request.getContentAsByteArray(), InterceptorConstants.UTF_8));
        }
        return null;
    }

    private String readBody(int httpStatus, byte[] responseBody) throws IOException {
        return HttpStatus.valueOf(httpStatus).is2xxSuccessful() ? readStatusCode(new ObjectMapper().readTree(responseBody)) : null;
    }

    private String readBody(int httpStatus, String responseBody) throws IOException {
        return HttpStatus.valueOf(httpStatus).is2xxSuccessful() ? readStatusCode(new ObjectMapper().readTree(responseBody)) : null;
    }

    private String readStatusCode(JsonNode respJson) {
        if (respJson.path("status").hasNonNull("code")) {
            return respJson.path("status").path("code").asText();
        } else if (respJson.path("status").hasNonNull("statuscode")) {
            return respJson.path("status").path("statuscode").asText();
        } else if (respJson.hasNonNull("responseCode")) {
            return respJson.path("responseCode").asText();
        }
        return null;
    }

    @Override
    public void destroy() {

    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Collections.list(request.getHeaderNames())
            .forEach(hKey -> headers.put(hKey.toLowerCase(), String.join(",", Collections.list(request.getHeaders(hKey)))));
        return headers;
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames().forEach(hKey -> headers.put(hKey.toLowerCase(), String.join(",", response.getHeaders(hKey))));
        return headers;
    }

    private String getClientIp(Map<String, String> headers, String remoteAddr) {
        String ip = headers.get(InterceptorConstants.X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.get(InterceptorConstants.PROXY_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.get(InterceptorConstants.WL_PROXY_CLIENT_IP);

        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.get(InterceptorConstants.HTTP_CLIENT_IP);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.get(InterceptorConstants.HTTP_X_FORWARDED_FOR);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = remoteAddr;
        }
        return ip;
    }
}
