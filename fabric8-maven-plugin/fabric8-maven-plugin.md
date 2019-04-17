# Introduction

This is a place to note down useful commands / tips for using [`fabric8-maven-plugin`](https://github.com/fabric8io/fabric8-maven-plugin).

Full official documentation (version 3.5.40 as of 2018-07-05):

1. [Online User Manual](http://maven.fabric8.io)

2. [PDF](https://fabric8io.github.io/fabric8-maven-plugin/fabric8-maven-plugin.pdf)


# Useful Commands

### Use this to deploy application to Minikube

```
mvn clean install fabric8:resource fabric8:build fabric8:apply
```

### Use this to push image to self-hosted Docker Registry

```
mvn clean install -Ddocker.registry=harbor.example.com -Ddocker.username=${your.username} -Ddocker.password=${your.password} fabric8: resource fabric8:build fabric8:push
```

>_Note: Currently there is a limitation where your Minikube must be up otherwise your push action will fail._


# Commonly-used Build Goals

| Goal                                          | Description                           |
| --------------------------------------------- | ------------------------------------- |
| [`fabric8:resource`](http://maven.fabric8.io/#fabric8:resource) | Create Kubernetes and OpenShift resource descriptors |
| [`fabric8:build`](http://maven.fabric8.io/#fabric8:build) | Build Docker images |
| [`fabric8:apply`](http://maven.fabric8.io/#fabric8:apply) | Applies the resources created with fabric8:resource to a connected Kubernetes / OpenShift cluster |
| [`fabric8:undeploy`](http://maven.fabric8.io/#fabric8:undeploy) | Delete the Kubernetes resources that you deployed via `fabric8:run`/`fabric8:deploy`/`fabric8:apply` goals |
| [`fabric8:push`](http://maven.fabric8.io/#fabric8:push) | Push Docker images to a registry  |

