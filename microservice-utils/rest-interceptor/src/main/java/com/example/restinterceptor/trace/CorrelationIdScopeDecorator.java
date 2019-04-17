package com.example.restinterceptor.trace;

import brave.Tracer;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * Adds {@linkplain MDC} properties "correlationId" when a {@link Tracer#currentSpan()} span is current.
 *
 * @author wai.loon.theng
 * @since 0.0.1.0
 * <p>
 * Reference: Slf4jScopeDecorator in {@link org.springframework.cloud.sleuth.log}
 */
@Slf4j
public class CorrelationIdScopeDecorator implements CurrentTraceContext.ScopeDecorator {

    private static final String CORRELATION_ID_NAME = "correlationId";

    @Override
    public CurrentTraceContext.Scope decorateScope(TraceContext currentSpan, CurrentTraceContext.Scope scope) {
        if (currentSpan != null) {
            String correlationId = ExtraFieldPropagation.get(currentSpan, CORRELATION_ID_NAME);
            MDC.put(CORRELATION_ID_NAME, correlationId);
            log.trace("Attached Correlation ID {} for span: {}", correlationId, currentSpan);
        } else {
            MDC.remove(CORRELATION_ID_NAME);
            log.trace("Removed Correlation ID from MDC");
        }
        return scope;
    }
}
