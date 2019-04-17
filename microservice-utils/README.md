# Maven Repositories
If you're unfamiliar with Maven, here's a [getting started guide](https://maven.apache.org/guides/getting-started/).

## Adding a Maven repository (e.g. Nexus)
Maven allows for a custom artifact repository to be added so projects can pull dependencies from them. Here's how to add one. For more information on settings.xml visit [here](https://maven.apache.org/settings.html)

1. Open or create a settings.xml (located in `~/.m2` for MacOS and `%userprofile%\.m2` for Windows)
2. Add a `mirror` attribute which contains an ID, name and the URL in the following format:
    ```xml
    <mirrors>
        <mirror>
            <id>nexus-id</id>
            <name>Generic Project Nexus</name>
            <url>https://your.host/nexus/content/groups/public/</url>
            <mirrorOf>*</mirrorOf>
        </mirror>
    </mirrors>
    ```
3. Add a `server` attribute which holds the credentials for the server in the following format:
    ```xml
    <servers>
        <server>
            <id>nexus-id</id>
            <username>yourusername</username>
            <password>yourpassword</password>
        </server>
    </servers>
    ```
4. Add a profile to include the repository for Maven. Another repository can be added with the `repository.snapshots.enabled` attribute set to false as a releases repository:
    ```xml
    <profiles>
        <profile>
            <id>maven.profile</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <repositories>
                <repository>
                    <id>repo.snapshot</id>
                    <name>Snapshot Repository</name>
                    <url>https://your.host/nexus/content/repositories/snapshots/</url>
                    <releases>
                        <enabled>false</enabled>
                    </releases>
                    <snapshots>
                        <updatePolicy>always</updatePolicy>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>repo.releases</id>
                    <name>Releases Repository</name>
                    <url>https://your.host/nexus/content/repositories/releases/</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>
    ```
5. To test if the settings are in effect, run `mvn compile` on a Maven project and ensure the packages downloaded are originating from your specified url

## Deploying artifact to custom repository
1. In the project pom.xml file, add the `distributionManagement` attribute:
    ```xml
    <distributionManagement>
        <snapshotRepository>
            <id>repo.snapshot</id>
            <name>Snapshot Repository</name>
            <url>https://your.host/nexus/content/repositories/snapshots/</url>
        </snapshotRepository>
        <repository>
            <id>repo.releases</id>
            <name>Releases Repository</name>
            <url>https://your.host/nexus/content/repositories/releases/</url>
        </repository>
    </distributionManagement>
    ```
2. To test the deployment settings are working, do a `mvn deploy` and ensure the generated artifact is being uploaded to the correct repository url