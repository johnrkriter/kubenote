# jib-maven-plugin

## Official Documentation
Public GitHub: https://github.com/GoogleContainerTools/jib/tree/master/jib-maven-plugin

## Java Project Setup
In your Maven Java project, add the plugin to your `pom.xml`.
>Note that the `docker.registry` would be the host name of your docker registry followed by project name, e.g. `harbor.example.com/projectalpha`.
```
<project>
    ...
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>com.google.cloud.tools</groupId>
                <artifactId>jib-maven-plugin</artifactId>
                <version>1.0.2</version>
                <configuration>
                    <from>
                        <image>openjdk:8-jre-alpine</image>
                    </from>
                    <to>
                        <image>${docker.registry}/${project.artifactId}:${project.version}</image>
                    </to>
                </configuration>
            </plugin>
            ...
        </plugins>
    </build>
    ...
</project>
```

## Build and Push from Local

### Setup `~/.m2/settings.xml`
Replace the `USERNAME` and `PASSWORD` accordingly.
```
<settings>
    ...
    <servers>
        ...
        <server>
            <id>harbor.example.com</id>
            <username>USERNAME</username>
            <password>PASSWORD</password>
        </server>
        ...
    </servers>
    ...
</settings>
```

### Build Docker Image
```
mvn clean compile jib:build -Ddocker.registry=harbor.example.com
```