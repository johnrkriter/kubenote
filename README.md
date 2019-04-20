# Kubenote
*Refactoring in progress.*

## Key Components in Microservices Architecture
* [Spring Cloud Kubernetes](spring-cloud-kubernetes) - Using k8s native service discovery for Spring Boot apps.
* [Spring Cloud Gateway](spring-cloud-gateway) - An API Gateway, providing a single point of entry in a microservices architecture.
* [Spring Cloud Config Server](spring-cloud-config/spring-cloud-config-server) - Centralized configuration.
* [Spring Boot Admin](spring-boot-admin) - Admin console for Spring Boot apps.
* [Spring Cloud Data Flow](helm/charts/stable/spring-cloud-data-flow) - Toolkit for building data integration and real-time data processing pipelines.
* Camunda Cockpit - WIP

## Microservices Features

| Feature | Details	|
| ----- | ---- |
| REST | See [Spring Cloud Kubernetes](spring-cloud-kubernetes) |
| [JDBC and Flyway](jdbc-and-flyway) | Relational database connection (JDBC) and data migration (Flyway). |
| Local caching with EhCache | See [Caching](caching) |
| Centralized Caching with Redis | See [Caching](caching) |
| Object Storage with S3 / Minio | See [storage-helper](microservice-utils/storage-helper) |
| [Spring Cloud Vault](spring-cloud-vault.md) | Secret management |
| [Micrometer, Prometheus, Grafana](spring-boot-micrometer-prometheus.md) | Metrics monitoring. WIP |
| [Netflix Hystrix](circuit-breaker/hystrix) | Circuit breaker |
| [Spring Cloud Stream](https://github.com/spring-cloud/spring-cloud-stream-samples) | Event-driven architecture |
| Lookup | WIP |
| Batches | [Spring Batch](https://spring.io/projects/spring-batch) or [Spring Cloud Task](https://spring.io/projects/spring-cloud-task) |
| MongoDB | See [spring-data-mongodb](https://spring.io/guides/gs/accessing-data-mongodb/) |
| Rule Engine using Camunda | WIP |
| BPM using Camunda | WIP |
| Swagger | Produce Swagger documentation automatically using Swagger Maven dependencies - WIP |
| Traceability with Spring Cloud Sleuth | See [log-helper](microservice-utils/log-helper) .WIP |

## Useful Utilities
| Feature | Details	|
| ----- | ---- |
| [parent-pom](microservice-utils/parent-pom) | A parent pom to govern version of common dependencies. |
| [rest-interceptor](microservice-utils/rest-interceptor) | To intercept REST requests/responses. |
| [httpheader-helper](microservice-utils/httpheader-helper) | A [HttpHeaders](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html) bean as a convenience to access HTTP headers of REST requests. |
| [stream-interceptor](microservice-utils/stream-interceptor) | To intercept Spring Cloud Stream messages. |
| [Customizing Cache Error Handler](microservice-utils/customizing-cache-error-handler) | Overriding `CacheErrorHandler` with `CachingConfigurerSupport`. |
| [storage-helper](microservice-utils/storage-helper) | To integrate with object storage platforms, i.e. AWS S3, Minio. |
| [cloud-config-helper](microservice-utils/cloud-config-helper) | Perform logging upon configuration refresh via Spring Cloud Bus |
| [event-helper](microservice-utils/event-helper) | Provide facility to publish audit/event log (in HashMap<String, Object> format) to Kafka. |
| [log-helper](microservice-utils/log-helper) | Configures console log to include additional fields for traceability purposes. |
| [Exception Handling](microservice-utils/exception-handling) | |
| [Helm Starter](helm-starter) | Customize your own Helm chart starter. |
| [jib-maven-plugin](jib-maven-plugin.md) | Build Docker image from source code using Maven plugin. |
| [helm-push](https://github.com/chartmuseum/helm-push) | Package and push a Helm chart to a chart museum in a single step. | 

## DevOps
* Set up Confluent Platform (OSS) - using [confluentinc/cp-helm-charts](https://github.com/confluentinc/cp-helm-charts/)
* Set up Hashicorp Vault with Consul backend - using [stable/consul](helm/charts/stable/consul) and [incubator/vault](helm/charts/incubator/vault)
* Set up MongoDB - using [stable/mongodb](helm/charts/stable/mongodb)
* Security using Keycloak Gatekeeper - using [custom/keycloak-gatekeeper](helm/charts/custom/keycloak-gatekeeper)
* Setting up Logstash for log aggregation - Refer [Logstash conf samples](logstash/conf)

## Kubernetes
* [Minikube](minikube) - Running Kubernetes locally

## Miscellaneous
* [Debugging MySQL pod in k8s](mysql-debugging.md)