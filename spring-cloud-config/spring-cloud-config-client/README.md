# config-client
This is a sample client app for Spring Cloud Config Server.

## Prerequisite
* Apache Kafka

## Features
* Connect to [Spring Cloud Config Server](../spring-cloud-config-server) using Kubernetes service discovery. A demo endpoint `GET /show-value` retrieving configuration from config server.
* With Spring Cloud Bus enabled and the class containing the `GET /show-value` is annotated with `@RefreshScope`, a hot refresh would be automatically performed when the configuration is updated at Config Server backend. 
