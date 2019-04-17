# Spring Cloud Kubernetes
* **Spring Cloud Kubernetes** provides service discovery to Spring Boot applications running in Kubernetes. It allows Spring Boot applications to communicate with each other directly.
* This is also known as an alternative to Netflix Eureka service discovery.   

## Official Documentation
Public GitHub: https://github.com/spring-cloud/spring-cloud-kubernetes

## Sample
* Deploy `tenant` and `neighbour` in the same namespace of a Kubernetes cluster.
* Curl `GET /neighbour/greet` of `tenant`, to observe `tenant` calls `neighbour` service using Ribbon.
* Curl `GET /tenant/hello` of `neighbour`, to observe `neighbour` calls `tenant` service using Ribbon.

## Getting Started

### 1. Add dependencies to `pom.xml`
Add the following dependencies to your `pom.xml` in your Spring Boot project.
>Note: If `spring-cloud-starter-netflix-eureka-client` dependency exists, remove it.

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-kubernetes</artifactId>
            <version>1.0.1.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-kubernetes-ribbon</artifactId>
            <version>1.0.1.RELEASE</version>
        </dependency>
```

### 2. Add `@EnableDiscoveryClient` to Main Application Class
>Note that `@EnableEurekaClient` has to be removed.

### 3. Good to go!
* Now you can proceed to deploy your Spring Boot application to Kubernetes, and your Ribbon should work automatically.
* Use a `RestTemplate` annotated with `@LoadBalanced` to call other services registered to Kubernetes under the same namespace.

## Running in local

1. Disable [Role-based Access Control (RBAC)]((https://kubernetes.io/docs/reference/access-authn-authz/rbac/)) to simplify the access of the applications.

    ```
    $ kubectl create clusterrolebinding permissive-binding \
    --clusterrole=cluster-admin \
    --group=system:serviceaccounts
    ```