package com.example.sba.sidecar;

import de.codecentric.boot.admin.server.cloud.discovery.DefaultServiceInstanceConverter;
import de.codecentric.boot.admin.server.cloud.discovery.ServiceInstanceConverter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnProperty(prefix = "spring.boot.admin.discovery", name = "enabled", matchIfMissing = true)
public class SideCarSupportConfig {

    @Bean
    @Primary
    @ConditionalOnMissingBean({ServiceInstanceConverter.class})
    @ConfigurationProperties(prefix = "spring.boot.admin.discovery.converter")
    public DefaultServiceInstanceConverter serviceInstanceConverter() {
        return new SideCarServiceInstanceConverter();
    }
}
