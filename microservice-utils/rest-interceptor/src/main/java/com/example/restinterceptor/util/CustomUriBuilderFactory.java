package com.example.restinterceptor.util;

import brave.propagation.ExtraFieldPropagation;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class CustomUriBuilderFactory extends DefaultUriBuilderFactory {

    public CustomUriBuilderFactory() {
        super();
    }

    public CustomUriBuilderFactory(String baseUriTemplate) {
        super(baseUriTemplate);
    }

    public CustomUriBuilderFactory(UriComponentsBuilder baseUri) {
        super(baseUri);
    }

    @Override
    public URI expand(String uriTemplate, Map<String, ?> uriVars) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(uriTemplate);
        ExtraFieldPropagation.set("baseUri", urlBuilder.encode().toUriString());
        return super.expand(uriTemplate, uriVars);
    }

    @Override
    public URI expand(String uriTemplate, Object... uriVars) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(uriTemplate);
        ExtraFieldPropagation.set("baseUri", urlBuilder.encode().toUriString());
        return super.expand(uriTemplate, uriVars);
    }
}
