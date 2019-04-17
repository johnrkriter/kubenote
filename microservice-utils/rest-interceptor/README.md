# REST Interceptor
`rest-interceptor` intercepts request and responses to perform pre and post processing actions such as logging.
Suggested to be used as part of a RestTemplate object so interceptors can be specified for all controllers from a single point
## Usage
1. Include in your current project's pom.xml `dependencies`
    ```xml
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>rest-interceptor</artifactId>
    </dependency>
    ```
2. Add `com.example` and your current package to the `scanBasePackages` attribute in your SpringBootApplication annotation, or change the package for the rest-interceptor project to match your own
    ```java
    @SpringBootApplication(scanBasePackages = { "com.example", "your.current.package" })
    ```
### Kafka logging configuration
  Set the values for the following properties in your `application.properties` file
  ```
  # Kafka Logging
  logging.enabled.kafka=true
  spring.kafka.logging.bootstrap-servers=localhost:9092
  stream-logging.kafka.broker=localhost:9092
  rest-interceptor.requestLogTopic=FE-REQUEST-LOG
  rest-interceptor.externalRequestLogTopic=FE-REQUEST-EXTERNAL-LOG
  rest-interceptor.internalRequestLogTopic=FE-REQUEST-INTERNAL-LOG
  ```
### Incoming request/response logging
  This is enabled by default when `logging.enabled.kafka` is set to `true`. All requests are filtered and logged to the topic provided in `rest-interceptor.requestLogTopic`
### Outgoing request/response logging
1. Set the interceptors for your RestTemplate
    ```java
    restTemplate.setInterceptors(
      Collections.unmodifiableList(
        Arrays.asList(
          new ExternalApiRequestLoggingInterceptor()
        )
      )
    );
    ```
2. Autowire and use your RestTemplate in your Service
    ```java
    @Autowired
    private RestTemplate restTemplate;
    ```