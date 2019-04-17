# Micrometer, Prometheus, Grafana

## Configuring Microservices
### Pod Annotation
```yaml
    prometheus.io/scrape: "true"
    prometheus.io/port: "8080"
    prometheus.io/path: "/actuator/prometheus"
```

### Maven Dependency
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

## Useful Dashboard
* [JVM (Micrometer)](https://grafana.com/dashboards/4701) - Note: Set `management.metrics.tags.application=${spring.application.name}` to monitor Spring Boot app on the dashboard.
* [SCDF Streams](https://github.com/spring-cloud/spring-cloud-dataflow/blob/master/src/grafana/prometheus/docker/grafana/dashboards/scdf-streams.json) - Note: Rename ScdfPrometheus datasource to Prometheus.  
* [Applications](https://github.com/spring-cloud/spring-cloud-dataflow/blob/master/src/grafana/prometheus/docker/grafana/dashboards/scdf-applications.json) - Note: Rename ScdfPrometheus datasource to Prometheus.


