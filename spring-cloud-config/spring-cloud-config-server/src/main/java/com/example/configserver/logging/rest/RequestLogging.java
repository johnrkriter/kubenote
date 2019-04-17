package com.example.configserver.logging.rest;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestLogging implements Serializable {
    private String timestamp;
    private String uri;
    private int httpStatusCode;
    private RequestSource source;

    RequestLogging(String timestamp, String uri, int httpStatusCode, String sourceService, String sourceHostname, String sourceIp) {
        this.timestamp = timestamp;
        this.uri = uri;
        this.httpStatusCode = httpStatusCode;
        this.source = new RequestSource(sourceService, sourceHostname, sourceIp);
    }

    @Override
    public String toString() {
        return "{" +
                "timestamp='" + timestamp + '\'' +
                ", uri='" + uri + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", source='" + source + '\'' +
                '}';
    }
}

