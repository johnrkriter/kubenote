package com.example.cloudconfighelper.model;

import java.io.Serializable;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ContextRefreshLog implements Serializable {
    private String id;
    private String timestamp;
    private String service;
    private String originService;
    private String destinationService;
    private Set<String> refreshedKeys;
    private String hostName;
    private String hostAddress;
}
