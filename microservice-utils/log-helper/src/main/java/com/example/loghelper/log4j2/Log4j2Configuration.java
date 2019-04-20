package com.example.loghelper.log4j2;

import brave.Tracing;
import brave.context.log4j2.ThreadContextScopeDecorator;
import brave.propagation.B3Propagation;
import brave.propagation.ExtraFieldPropagation;
import brave.propagation.ThreadLocalCurrentTraceContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Log4j2Configuration {
	@Bean
	public Tracing tracing(@Value("${spring.application.name}") String serviceName) {
		return Tracing.newBuilder()
				.localServiceName(serviceName)
				.propagationFactory(ExtraFieldPropagation.newFactory(B3Propagation.FACTORY, "correlationId"))
				.currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
						.addScopeDecorator(ThreadContextScopeDecorator.create())
						.addScopeDecorator(new ExtraFieldScopeDecorator("correlationId"))
						.build()
				)
				.build();
	}
}
