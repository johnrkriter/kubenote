package com.example.restinterceptor.config;

import java.nio.charset.Charset;

public class InterceptorConstants {

    // HEADER
    public static final String CORRELATION_ID = "correlationid";
    public static final String REQUEST_UID = "requestUID";
    public static final String RESOURCE_OWNER_ID = "resourceOwnerID";
    public static final String API_AUTH = "api-auth";
    public static final String API_SCOPE = "api-scope";
    public static final String API_KEY = "apikey";
    public static final String API_SECRET = "apisecret";
    public static final String USER_ID = "userid";
    public static final String SOURCE = "source";
    public static final String ACCEPT_LANG = "accept-language";
    public static final String LOGIN_ID = "loginid";
    public static final String CHANNEL_ID = "channelid";
    public static final String X_FORWARDED_FOR = "x-forwarded-for";
    public static final String PROXY_CLIENT_IP = "proxy-client-ip";
    public static final String WL_PROXY_CLIENT_IP = "wl-proxy-client-ip";
    public static final String HTTP_CLIENT_IP = "http_client_ip";
    public static final String HTTP_X_FORWARDED_FOR = "http_x_forwarded_for";

    public static final String EXTERNAL = "external";
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String ENET = "ENET";
    public static final String JSON_CONTENT_TYPE = "application/json";

    private InterceptorConstants() {
        throw new IllegalAccessError();
    }
}
