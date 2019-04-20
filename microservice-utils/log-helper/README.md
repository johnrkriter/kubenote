# Log Helper
`log-helper` configures console log to include additional fields for traceability purposes. 
* `spring.application.name`, make sure to configure it in **bootstrap.properties**.
* `traceId` and `spanId` from [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth).
* Extra field from HTTP header. By default, it propagates `correlationId`.   

> Note: `log-helper` uses Log4j2.

## How to use
1. Add dependency to `pom.xml`. 
	```xml
	<dependency>
		<groupId>com.example</groupId>
		<artifactId>log-helper</artifactId>
		<version>X.Y.Z-SNAPSHOT</version>
	</dependency>
	```
2. Configure `spring.application.name` in **bootstrap.properties**.
3. Perform logging using `@Log4j2`. Sample output as below.
	* `spring.application.name=demodemo`
	* `traceId` is f7b518334ee1d87c
	* `spanId` is f7b518334ee1d87c
	* `traceId` is f7b518334ee1d87c
	```bash
	2019-04-20 14:52:39.258  INFO [demodemo,f7b518334ee1d87c,f7b518334ee1d87c,3] 50823 --- [nio-8080-exec-1] c.e.MyController                         : logging with Log4j2 - hello! 3
	```