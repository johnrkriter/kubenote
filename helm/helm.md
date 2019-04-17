# Helm
[**Helm**](https://helm.sh) is a tool that streamlines installing and managing Kubernetes applications. Think of it like apt/yum/Homebrew for Kubernetes.

Helm can be used to install and run public open-source applications on Kubernetes, e.g. `helm install stable/mysql` to install MySQL.

By creating our own Helm charts, Helm can also be used to deploy custom microservices on Kubernetes.

## Helm Chart
Think of a Helm chart like a Kubernetes manifest file (YAML) that is decomposed into two parts:
  1. templates
  2. values.yaml

### Sample of Kubernetes Manifest YAML 
```yaml
apiVersion: v1
kind: Pod
metadata:
  name: memory-demo
  namespace: mem-example
spec:
  containers:
  - name: memory-demo-ctr
    image: polinux/stress
    resources:
      limits:
        memory: "200Mi"
      requests:
        memory: "100Mi"
    command: ["stress"]
    args: ["--vm", "1", "--vm-bytes", "150M", "--vm-hang", "1"]
```

### Sample of Helm Chart
See [sample-helm-chart](sample-helm-chart). 

## Creating Helm Chart for Microservices
Use [`helm create`](https://helm.sh/docs/helm/#helm-create) to initialize a Helm chart. 

> Tips: Use a [Helm Starter](../helm-starter) to initialize with a customized starter.

## Push Chart to Chart Museum
Use [`helm push`](https://github.com/chartmuseum/helm-push) to package and push a chart to a chart museum in a single step. 

