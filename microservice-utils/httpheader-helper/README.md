# HTTP Header Helper
`httpheader-helper` captures HTTP request headers and construct them as a request-scoped [HttpHeaders](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/HttpHeaders.html) bean. The bean allows developers to access the headers from any classes by autowiring.
## Usage
1. Include in your current project's pom.xml `dependencies`
    ```xml
    <dependency>
      <groupId>com.example</groupId>
      <artifactId>httpheader-helper</artifactId>
      <version>X.Y.Z-SNAPSHOT</version>
    </dependency>
    ```
2. Autowire the `HttpHeaders` class into your Controller class
    ```java
    @Autowired
    private HttpHeaders headers;
    ```
3. Access the headers as needed
    ```java
    return new ResponseEntity<>(response, headers, HttpStatus.OK);
    ```