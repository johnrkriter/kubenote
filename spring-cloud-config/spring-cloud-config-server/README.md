# config-server
`config-server` is an implementation of Spring Cloud Config Server.

## Prerequisite
* Apache Kafka

## Features
* Using git backend.
* Spring Cloud Config Monitor enabled.
* Request logging using Spring Cloud Stream.

## Spring Cloud Config Monitor
To enable hot refresh of configurations using [Spring Cloud Config Monitor](https://cloud.spring.io/spring-cloud-config/multi/multi__push_notifications_and_spring_cloud_bus.html), add a webhook to your git server to the `POST /monitor` endpoint of this `config-server`.