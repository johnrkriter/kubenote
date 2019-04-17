package com.example.storagehelper.config.minio;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.storagehelper.service.MinioStorageHelper;
import com.example.storagehelper.service.MockStorageHelper;
import com.example.storagehelper.service.StorageHelper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.Assert;

/**
 * @author c.horprasertwong
 */
@Conditional(MinioCondition.class)
@Configuration
@Slf4j
public class MinioConfiguration {

    @Value("${storage-helper.minio.use-mock:false}")
    private String useMock;
    @Value("${storage-helper.minio.endpoint:#{null}}")
    private String endpoint;
    @Value("${storage-helper.minio.use-eureka:true}")
    private String useEureka;
    // Required if useEureka = true
    @Value("${storage-helper.minio.service-name:#{null}}")
    private String serviceName;
    @Value("${storage-helper.minio.accessKey:#{null}}")
    private String accessKey;
    @Value("${storage-helper.minio.secretKey:#{null}}")
    private String secretKey;
    // Why can't I simply define default value here?
    @Value("${storage-helper.signer.override:#{null}}")
    private String signerOverride;

    @Value("${storage-helper.singing.region:ap-southeast-1}")
    private String signingRegion;

    @Value("${storage-helper.socket-timeout.in.millisecs:#{null}}")
    private Integer socketTimeout;

    @Value("${storage-helper.request-timeout.in.millisecs:#{null}}")
    private Integer requestTimeout;

    @Value("${storage-helper.max-connections-pool:#{null}}")
    private Integer maxConnectionPool;

    @Value("${storage-helper.max-consecutive-retries-before-throttling:#{null}}")
    private Integer maxConsecutiveRetriesBeforeThrottling;

    @Bean("minioStorageHelper")
    @Primary
    public StorageHelper minioStorageHelper() {
        String minioEndpoint;
        Assert.hasLength(useMock, "storage-helper.minio.use-mock MUST be true/false");
        if (useMock.contains("true")) {
            return new MockStorageHelper();
        }
        else {
            Assert.hasLength(accessKey, "storage-helper.minio.accessKey is needed");
            Assert.hasLength(secretKey, "storage-helper.minio.secretKey is needed");
        }

        Assert.hasLength(endpoint, "Endpoint must not null if useEureka = false");
        minioEndpoint = endpoint;

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        if (signerOverride != null) {
            clientConfiguration.setSignerOverride(signerOverride);
        }
        else {
            clientConfiguration.setSignerOverride("AWSS3V4SignerType");
        }

        // Set socket timeout if defined - for unresponsive server
        if (socketTimeout != null) {
            clientConfiguration.setSocketTimeout(socketTimeout);
        }

        // Set request timeout - upper boundary limit for requests
        if (socketTimeout != null) {
            clientConfiguration.setRequestTimeout(requestTimeout);
        }

        // Set max connection pool if defined
        if (maxConnectionPool != null) {
            clientConfiguration.setMaxConnections(maxConnectionPool);
        }

        // Set max consecutive retries if defined
        if (maxConsecutiveRetriesBeforeThrottling != null) {
            clientConfiguration.setMaxConsecutiveRetriesBeforeThrottling(maxConsecutiveRetriesBeforeThrottling);
        }

        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                minioEndpoint,
                                signingRegion
                        )
                )
                .withPathStyleAccessEnabled(true)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
        return new MinioStorageHelper(s3Client);
    }
}
