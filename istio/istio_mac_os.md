# 1. Prerequisites

Follow steps 1-4 in this [snippet](../minikube/minikube_mac_os.md) to set up your local environment before running Istio.

# 2. Download and Prepare to Install Istio

Starting with the 0.2 release, Istio is installed in its own `istio-system` namespace, and can manage services from all other namespaces.

### Download Istio
```
curl -L https://git.io/getLatestIstio | sh -
```

Change directory to the istio package. For example, if the package is istio-0.8.0:
```
cd istio-0.8.0
```

Add the `istioctl` client to your PATH. For example, run the following command on a MacOS or Linux system:
```
export PATH=$PWD/bin:$PATH
```

### Installation command
```
kubectl apply -f install/kubernetes/istio-demo.yaml
```

# 3. Verify the installation
Ensure the following Kubernetes services are deployed:
```
$ kubectl get svc -n istio-system
NAME                       TYPE           CLUSTER-IP   EXTERNAL-IP     PORT(S)                                                               AGE
istio-citadel              ClusterIP      30.0.0.119   <none>          8060/TCP,9093/TCP                                                     7h
istio-egressgateway        ClusterIP      30.0.0.11    <none>          80/TCP,443/TCP                                                        7h
istio-ingressgateway       LoadBalancer   30.0.0.39    9.111.255.245   80:31380/TCP,443:31390/TCP,31400:31400/TCP                            7h
istio-pilot                ClusterIP      30.0.0.136   <none>          15003/TCP,15005/TCP,15007/TCP,15010/TCP,15011/TCP,8080/TCP,9093/TCP   7h
istio-policy               ClusterIP      30.0.0.242   <none>          9091/TCP,15004/TCP,9093/TCP                                           7h
istio-statsd-prom-bridge   ClusterIP      30.0.0.111   <none>          9102/TCP,9125/UDP                                                     7h
istio-telemetry            ClusterIP      30.0.0.246   <none>          9091/TCP,15004/TCP,9093/TCP,42422/TCP                                 7h
prometheus                 ClusterIP      30.0.0.253   <none>          9090/TCP       
```

Ensure the corresponding Kubernetes pods are deployed and all containers are up and running:
```
$ kubectl get pods -n istio-system
NAME                                       READY     STATUS      RESTARTS   AGE
istio-citadel-dcb7955f6-vdcjk              1/1       Running     0          11h
istio-egressgateway-56b7758b44-l5fm5       1/1       Running     0          11h
istio-ingressgateway-56cfddbd5b-xbdcx      1/1       Running     0          11h
istio-pilot-cbd6bfd97-wgw9b                2/2       Running     0          11h
istio-policy-699fbb45cf-bc44r              2/2       Running     0          11h
istio-statsd-prom-bridge-949999c4c-nws5j   1/1       Running     0          11h
istio-telemetry-55b675d8c-kfvvj            2/2       Running     0          11h
prometheus-86cb6dd77c-5j48h                1/1       Running     0          11h
```

# 4. Deploy your application
### On IntelliJ
Run this line to use minikube's daemon.
```
eval $(minikube docker-env)
```

Continue to follow steps 5-6 in this [snippet](../minikube/minikube_mac_os.md) to build and deploy your application using **fabric8-maven-plugin**
