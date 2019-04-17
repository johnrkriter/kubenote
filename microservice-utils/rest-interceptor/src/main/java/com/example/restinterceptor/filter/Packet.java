package com.example.restinterceptor.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Packet implements Serializable {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date tranTime;
    private long executeTime;
    private String serverName;
    private int serverPort;
    private String remoteServer;
    private int remotePort;
    private String contextPath;
    private String servletPath;
    private String scheme;
    private String localAddr;
    private String reqMethod;
    private String reqParams;
    private String reqQueryParams;
    private int reqContentLength;
    private String reqBody;
    private Map<String, String> reqHeaders;
    private String clientIp;
    private String uri;
    private int respHttpStatus;
    private int respContentLength;
    private String respStatus;
    private String respBody;
    private Map<String, String> respHeaders;
    private String traceId;
    private String spanId;
    private String parentSpanId;
    private String service;
    private String url;
    private String rawUri;

    public Packet(String id) {
        this.id = id;
    }
}