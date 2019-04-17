package com.example.configserver.logging.rest;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestSource implements Serializable {
    private String service;
    private String hostname;
    private String ip;

    RequestSource(String service, String hostname, String ip) {
        this.service = service;
        this.hostname = hostname;
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "{" +
                "service='" + service + '\'' +
                ", hostname='" + hostname + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
