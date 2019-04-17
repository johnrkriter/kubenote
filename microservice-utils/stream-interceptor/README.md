# Stream Interceptor
**Stream Interceptor** intercepts Spring Cloud Stream messages using [ChannelInterceptor](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/messaging/support/ChannelInterceptor.html) interface. The key feature is to log the stream message to Apache Kafka. 

## Getting Started (using Maven)
1.	Deploy to a Maven repository (e.g. Sonatype Nexus)
2.	Add dependency to `pom.xml` of the microservice.

	<dependency>
		<groupId>com.example</groupId>
		<artifactId>stream-interceptor</artifactId>
		<version>X.Y.Z-SNAPSHOT</version>
	</dependency>
	
3.	Configuration (WIP)