package com.example.httpheaderhelper.hystrix;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

/**
 * A Callable class that will invoke the Hystrix-protected method
 *      using session-scoped and request-scoped Spring Beans from parent thread
 */
@Slf4j
public final class RequestScopeConcurrencyCallable<V> implements Callable<V> {

    private final Callable<V> delegate;
    private RequestAttributes requestAttributes;

    RequestScopeConcurrencyCallable(Callable<V> delegate, RequestAttributes requestAttributes) {
        this.delegate = delegate;
        this.requestAttributes = requestAttributes;
    }

    /**
     * This function is invoked before the method protected by the @HystrixCommand annotation.
     *
     * It sets Request Attributes (session-scoped and request-scoped Spring Beans from parent thread)
     *      to ThreadLocal Request Attributes, which is associated with the child thread running the Hystrix-protected method.
     */
    @Override
    public V call() throws Exception {

        // Set Request Attributes from parent thread to child thread
        RequestContextHolder.setRequestAttributes(requestAttributes);

        // Invoking Hystrix protected method
        try {
            return delegate.call();
        }
        finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

}
