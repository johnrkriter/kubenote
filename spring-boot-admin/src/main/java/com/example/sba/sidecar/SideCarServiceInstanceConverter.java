package com.example.sba.sidecar;

import java.net.URI;

import de.codecentric.boot.admin.server.cloud.discovery.DefaultServiceInstanceConverter;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Allows instances to set their management endpoint using `management.url` property.
 * Originally designed to support Netflix Sidecar applications.
 */
public class SideCarServiceInstanceConverter extends DefaultServiceInstanceConverter {
    private static final String KEY_MANAGEMENT_URL = "management.url";

    @Override
    protected URI getManagementUrl(ServiceInstance instance) {
        String managementUrl = instance.getMetadata().get(KEY_MANAGEMENT_URL);
        if (isEmpty(managementUrl)) {
            return super.getManagementUrl(instance);
        } else {
            return UriComponentsBuilder.fromHttpUrl(managementUrl).build().toUri();
        }
    }
}
