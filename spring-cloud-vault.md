# Spring Cloud Vault
> **Spring Cloud Vault Config** provides client-side support for externalized configuration in a distributed system. With [HashiCorp Vault](https://vaultproject.io/) you have a central place to manage external secret properties for applications across all environments. Vault can manage static and dynamic secrets such as username/password for remote applications/resources and provide credentials for external services such as MySQL, PostgreSQL, Apache Cassandra, MongoDB, Consul, AWS and more.

Read: https://cloud.spring.io/spring-cloud-vault/

## Maven Dependency
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

## Configuration
Configure the Vault connection in [`bootstrap.properties`](https://cloud.spring.io/spring-cloud-static/spring-cloud-commons/2.1.1.RELEASE/multi/multi__spring_cloud_context_application_context_services.html#_the_bootstrap_application_context).

### Discover Vault instance using [Spring Cloud Kubernetes](https://spring.io/projects/spring-cloud-kubernetes)
```properties
spring.cloud.vault.enabled=true
spring.cloud.vault.discovery.enabled=false
spring.cloud.vault.token=s.4fhaikfwiugibfi38guibfav8
spring.cloud.vault.scheme=http
# FQDN direct uri http://{serviceId}.{namespace}:{port}
spring.cloud.vault.uri=http://vault-vault.vault:8200
```

### Disabling Vault instance
> Note: Useful for local profile
```properties
spring.cloud.vault.enabled=false
spring.cloud.config.discovery.enabled=false
```