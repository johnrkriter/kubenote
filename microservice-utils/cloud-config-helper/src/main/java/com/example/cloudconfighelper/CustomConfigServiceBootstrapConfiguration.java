package com.example.cloudconfighelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;

/**
 * Deprecated. To use, please revise.
 * In addition, this is potentially redundant. Validate if use.
 *
 * @see <a href="https://github.com/spring-cloud/spring-cloud-config/blob/master/spring-cloud-config-client/src/main/java/org/springframework/cloud/config/client/ConfigServiceBootstrapConfiguration.java">ConfigServiceBootstrapConfiguration source code</a>
 * @see <a href="https://github.com/spring-cloud/spring-cloud-config/blob/master/spring-cloud-config-client/src/main/java/org/springframework/cloud/config/client/ConfigServicePropertySourceLocator.java">ConfigServicePropertySourceLocator source code</a>
 * @see <a href="https://github.com/spring-cloud/spring-cloud-config/blob/master/spring-cloud-config-client/src/main/resources/META-INF/spring.factories">spring.factories sample</a>
 *
 */
@Deprecated
//@Configuration
public class CustomConfigServiceBootstrapConfiguration {

    private static final String SERVICE = "ms_service";
    private static final String HOSTNAME = "ms_hostname";
    private static final String IP = "ms_ip";

    @Primary
    @Bean
    public ConfigServicePropertySourceLocator configServicePropertySourceLocator(ConfigClientProperties clientProperties, @Value("${spring.application.name}") String serviceName) {
        ConfigServicePropertySourceLocator configServicePropertySourceLocator =  new ConfigServicePropertySourceLocator(clientProperties);
        configServicePropertySourceLocator.setRestTemplate(getSecureRestTemplate(clientProperties, serviceName));
        return configServicePropertySourceLocator;
    }

    // Reference : private org.springframework.cloud.config.client.ConfigServicePropertySourceLocator.getSecureRestTemplate
    private RestTemplate getSecureRestTemplate(ConfigClientProperties client, String serviceName) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout((60 * 1000 * 3) + 5000); //TODO 3m5s, make configurable?
        RestTemplate template = new RestTemplate(requestFactory);
        String username = client.getUsername();
        String password = client.getPassword();
        String authorization = client.getHeaders().get(AUTHORIZATION);
        Map<String, String> headers = new HashMap<>(client.getHeaders());

        if (password != null && authorization != null) {
            throw new IllegalStateException(
                    "You must set either 'password' or 'authorization'");
        }

        if (password != null) {
            byte[] token = Base64Utils.encode((username + ":" + password).getBytes());
            headers.put("Authorization", "Basic " + new String(token));
        } else if (authorization != null) {
            headers.put("Authorization", authorization);
        }

        if (!headers.isEmpty()) {
            template.setInterceptors(
                    Collections.singletonList(new ConfigServicePropertySourceLocator.GenericRequestHeaderInterceptor(headers))
            );
        }

        // Add Custom Interceptor to RestTemplate
        template.getInterceptors().add((httpRequest, bytes, clientHttpRequestExecution) -> {
            httpRequest.getHeaders().add(SERVICE, serviceName);
            httpRequest.getHeaders().add(HOSTNAME, ContextRefreshLogger.getHostName());
            httpRequest.getHeaders().add(IP, ContextRefreshLogger.getHostAddress());
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        });

        return template;
    }
}
