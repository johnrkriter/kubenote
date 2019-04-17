package com.example.restinterceptor.util;

import brave.propagation.ExtraFieldPropagation;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

public class InterceptorUtil {
    private InterceptorUtil() {
    }

    public static String getPath(URI uri) {
        String baseUri = ExtraFieldPropagation.get("baseUri");
        if (baseUri != null && !baseUri.isEmpty()) {
            try {
                return new URL(baseUri).getPath();
            } catch (MalformedURLException e) {
                return uri.getPath();
            }
        } else {
            return uri.getPath();
        }
    }

    public static String generateUUID32() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generateUUID36() {
        return UUID.randomUUID().toString();
    }
}
