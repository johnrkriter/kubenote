# Prepare Spring Boot Microservices for Helm Deployment to Kubernetes
## Developer
1. Helm Chart
2. Jenkinsfile
3. Ansible
4. Spring Cloud Kubernetes
5. Jib Maven Plugin
6. Prepare configurations and secrets in Spring Cloud Config Server, Hashicorp Vault, Kubernetes Secret
7. [Optional] Create MySQL database schema
8. [Optional] Set `server.port=8080`
9. [Optional] Ensure using default `#management.endpoints.web.base-path`

## DevOps
1. Setup image pull secret
2. Setup Jenkins job

## Deployment
1. Jib build
2. Push Helm chart
