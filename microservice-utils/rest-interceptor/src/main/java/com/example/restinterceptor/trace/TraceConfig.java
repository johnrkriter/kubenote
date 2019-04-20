package com.example.restinterceptor.trace;

import brave.propagation.CurrentTraceContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Reference: {@link org.springframework.cloud.sleuth.log.SleuthLogAutoConfiguration}
 */
@Deprecated
//@Configuration
//@PropertySource("classpath:rest-interceptors.properties")
public class TraceConfig {

    @Bean
    @ConditionalOnProperty(value = "rest-logging.correlationid-propagation.enabled", matchIfMissing = true)
    public CurrentTraceContext.ScopeDecorator correlationIdScopeDecorator() {
        return new CorrelationIdScopeDecorator();
    }
}
