package com.example.restinterceptor.interceptor;

import com.example.restinterceptor.config.BufferingClientHttpResponseWrapper;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;

import static com.example.restinterceptor.config.InterceptorConstants.*;
import static com.example.restinterceptor.util.InterceptorUtil.generateUUID32;

/**
 * This interceptor inserts Enterprise HTTP Headers.
 * <p>
 * Mandatory Headers:
 * (1) apiKey
 * (2) apiSecret
 * (3) resourceOwnerID
 * (4) requestUID
 */
@Setter
@AllArgsConstructor
public class EnterpriseHeaderInterceptor implements ClientHttpRequestInterceptor {

    private String apiKey;
    private String apiSecret;
    private boolean returnRequestUID = true;
    private boolean useApiAuthentication = true;

    public EnterpriseHeaderInterceptor() {
        this.useApiAuthentication = false;
    }

    public EnterpriseHeaderInterceptor(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public void setUseApiAuthentication(boolean useApiAuthentication) {
        if (StringUtils.isEmpty(this.apiKey) && StringUtils.isEmpty(this.apiSecret) && useApiAuthentication) {
            throw new IllegalArgumentException("useApiAuthentication cannot be true when apiKey and apiSecret are not set.");
        }
        this.useApiAuthentication = useApiAuthentication;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        // apiKey & apiSecret
        if (useApiAuthentication) {
            request.getHeaders().set(API_KEY, apiKey);
            request.getHeaders().set(API_SECRET, apiSecret);
        }

        // resourceOwnerID: Use `userId` header value if exists and 30-character long; otherwise default to "ENET".
        String userId = request.getHeaders().getFirst(USER_ID);
        if (!StringUtils.isEmpty(userId) && userId.length() == 30) {
            request.getHeaders().put(RESOURCE_OWNER_ID, Collections.singletonList(userId));
        } else {
            request.getHeaders().put(RESOURCE_OWNER_ID, Collections.singletonList(ENET));
        }

        // requestUID: UUID with hyphens removed (32-character).
        String requestUid = generateUUID32();
        request.getHeaders().set(REQUEST_UID, requestUid);
        if (returnRequestUID) {
            try (ClientHttpResponse response = execution.execute(request, body)) {
                // Return requestUID to caller
                response.getHeaders().set(REQUEST_UID, requestUid);
                return new BufferingClientHttpResponseWrapper(response);
            }
        } else {
            return execution.execute(request, body);
        }
    }

}
