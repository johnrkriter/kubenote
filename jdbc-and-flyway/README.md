# JDBC and Flyway

## Getting Started (MySQL)
1. Add Maven dependencies to POM.xml
	```xml
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-jdbc</artifactId>
	</dependency>
	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
	</dependency>
	<dependency>
		<groupId>org.flywaydb</groupId>
		<artifactId>flyway-core</artifactId>
	</dependency>
	```
2. Configure database connection string and connection pool settings
	```properties
	# JDBC (Flyway detects this automatically)
	spring.datasource.url=jdbc:mysql://localhost:3306/k8s_ms_a
	spring.datasource.username=root
	spring.datasource.password=
	
	# Hikari Connection Pool (HikariCP is the default JDBC connection pool implementation since Spring Boot 2)
	spring.datasource.hikari.connection-timeout=30000
	spring.datasource.hikari.validation-timeout=5000
	spring.datasource.hikari.idle-timeout=60000
	spring.datasource.hikari.max-lifetime=1800000
	spring.datasource.hikari.maximum-pool-size=10
	spring.datasource.hikari.initialization-fail-timeout=1
	```
3. Place Flyway scripts into `resources/db/migration` directory

## Miscellaneous
### [Maven Flyway Plugin](https://flywaydb.org/documentation/maven/)
`flyway-maven-plugin` allows executing Flyway action using Maven command, e.g. `mvn flyway:migrate`.
```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
</plugin>
```

### Spring Boot Actuator for Flyway
See https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/actuator-api/html/#flyway