# Parent POM
A parent pom to govern version of common dependencies.

## Getting Started
1.	Deploy to a Maven repository (e.g. Sonatype Nexus)
2.	Add dependency to `pom.xml` of the microservice.

	<dependency>
		<groupId>com.example</groupId>
		<artifactId>parent-pom</artifactId>
		<version>X.Y.Z-SNAPSHOT</version>
	</dependency>