package com.example.loghelper.log4j2;

import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.TraceContext;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

/**
 * Propagate extra field (e.g. Correlation ID) in Log4j2 thread context.
 *
 * @author wai.loon.theng
 *
 * Ref:
 * 	- {@link brave.context.log4j2.ThreadContextScopeDecorator}
 * 	- {@link brave.internal.propagation.CorrelationFieldScopeDecorator}
 */
@Log4j2
public class ExtraFieldScopeDecorator implements CurrentTraceContext.ScopeDecorator {

	private final String extraField;

	ExtraFieldScopeDecorator(String extraField) {
		this.extraField = extraField;
	}

	@Override
	public CurrentTraceContext.Scope decorateScope(TraceContext currentSpan, CurrentTraceContext.Scope scope) {
		String previousCorrelationId = get(extraField);

		if (currentSpan != null) {
			String extraFieldValue = ExtraFieldPropagation.get(currentSpan, extraField);
			put(extraField, extraFieldValue);
			log.trace("Attached {} with value {} for span: {}", extraField, extraFieldValue, currentSpan);
		} else {
			remove(extraField);
			log.trace("Removed {} from MDC", extraField);
		}

		class CorrelationFieldCurrentTraceContextScope implements CurrentTraceContext.Scope {
			@Override
			public void close() {
				scope.close();
				replace(extraField, previousCorrelationId);
			}
		}
		return new CorrelationFieldCurrentTraceContextScope();
	}

	private void replace(String key, @Nullable String value) {
		if (value != null) {
			put(key, value);
		} else {
			remove(key);
		}
	}

	private String get(String key) {
		return ThreadContext.get(key);
	}

	private void put(String key, String value) {
		ThreadContext.put(key, value);
	}

	private void remove(String key) {
		ThreadContext.remove(key);
	}

}
