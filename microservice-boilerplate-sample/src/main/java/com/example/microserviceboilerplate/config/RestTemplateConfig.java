package com.example.microserviceboilerplate.config;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import com.example.restinterceptor.interceptor.TraceabilityHeaderInterceptor;
import com.example.restinterceptor.interceptor.external.ExternalApiRequestLoggingInterceptor;
import com.example.restinterceptor.util.CustomUriBuilderFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${rest.max-total-connections}")
    private int maxTotalConnections;
    @Value("${rest.default-max-connections-per-route}")
    private int defaultMaxConnectionsPerRoute;
    @Value("${rest.connection-request-timeout}")
    private int connectionRequestTimeout;
    @Value("${rest.connect-timeout}")
    private int connectTimeout;
    @Value("${rest.read-timeout}")
    private int readTimeout;

    /**
     * HTTP client with customized HTTP connection pool
     */
    @Bean
    public HttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        HttpClient defaultHttpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(defaultMaxConnectionsPerRoute);
        return defaultHttpClient;
    }

    /**
     * HTTP request factory with customized HTTP timeout settings
     */
    @Bean
    public HttpComponentsClientHttpRequestFactory customHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient());
        factory.setConnectionRequestTimeout(connectionRequestTimeout);
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    /**
     * Netflix Ribbon load-balanced REST Template for connection to other microservices
     */
    @LoadBalanced
    @Bean(name = "loadBalancedRestTemplate")
    public RestTemplate loadBalancedRestTemplate(HttpComponentsClientHttpRequestFactory customHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(customHttpRequestFactory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setInterceptors(Collections.singletonList(traceabilityHeaderInterceptor()));
        return restTemplate;
    }
    @Bean
    public TraceabilityHeaderInterceptor traceabilityHeaderInterceptor() {
        return new TraceabilityHeaderInterceptor();
    }

    /**
     * Default REST Template
     */
    @Primary
    @Bean
    public RestTemplate normalRestTemplate(
            HttpComponentsClientHttpRequestFactory customHttpRequestFactory,
            ExternalApiRequestLoggingInterceptor externalApiRequestLoggingInterceptor
    ) {
        RestTemplate restTemplate = new RestTemplate(customHttpRequestFactory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setInterceptors(
                Collections.unmodifiableList(
                        Collections.singletonList(
                                // Add interceptors here if required
                                externalApiRequestLoggingInterceptor
                        )
                )
        );
        restTemplate.setUriTemplateHandler(new CustomUriBuilderFactory());
        return restTemplate;
    }

}
