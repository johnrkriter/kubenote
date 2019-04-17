package com.example.storagehelper.config.minio;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author c.horprasertwong
 */
public class MinioCondition implements Condition {
    private static final String ENABLE_MINIO = "storage-helper.minio.enabled";
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        String config = conditionContext.getEnvironment().getProperty(ENABLE_MINIO);
        return config != null
                && (config.contains("true") || config.contains("1"));
    }
}
