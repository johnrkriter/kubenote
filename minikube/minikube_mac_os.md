# 1. Prerequisites

### Install Docker for Mac
https://store.docker.com/editions/community/docker-ce-desktop-mac

### Install Minikube
```
brew update && brew cask install minikube
```

### Verify Installs
```
docker --version            # Docker version 18.03.1-ce, build 9ee9f40
docker-compose --version    # docker-compose version 1.21.1, build 5a3f1a3
docker-machine --version    # docker-machine version 0.14.0, build 89b8332
minikube version            # minikube version: v0.28.0
kubectl version --client    # Client Version: version.Info{Major:"1", Minor:"11", GitVersion:"v1.11.0", GitCommit:"91e7b4fd31fcd3d5f436da26c980becec37ceefe", GitTreeState:"clean", BuildDate:"2018-06-27T22:29:25Z", GoVersion:"go1.10.3", Compiler:"gc", Platform:"darwin/amd64"}
```

# 2. Start Minikube
```
minikube start --vm-driver=hyperkit
```
_Note 1: `--insecure-registry` is not required._
_Note 2: Specify `--vm-driver=hyperkit` to use Hyperkit VM, which seem to consume less resources than VirtualBox._

This can take a while, expected output:
```
Starting local Kubernetes cluster...
Kubectl is now configured to use the cluster.
```
Great! You now have a running Kubernetes cluster locally. Minikube started a virtual machine for you, and a Kubernetes cluster is now running in that VM.

### Check k8s
```
$ kubectl get nodes
NAME       STATUS    ROLES     AGE       VERSION
minikube   Ready     master    1m        v1.10.0
```

# 3. Use Minikube's built-in Docker Daemon
```
eval $(minikube docker-env)
```

**Important!!**
This step is to ensure you are pointing to the Minikube's Docker when running any Docker command, e.g. upload/run image, etc.
Make sure you run these commands before running registry and deploying your Spring Boot app later.
Alternatively, add this line to `.bash_profile` or `.zshrc` or ... to use minikube's daemon by default, but you'll see annoying error message every time you open your terminal when Minikube is not running.

_Note: You can revert back to the host docker daemon by running._
```
eval $(docker-machine env -u)
```

### Verify Docker Daemon
```
$ docker ps
CONTAINER ID        IMAGE                                      COMMAND                  CREATED              STATUS              PORTS               NAMES
5c555c438666        k8s.gcr.io/k8s-dns-sidecar-amd64           "/sidecar --v=2 --lo…"   14 seconds ago       Up 13 seconds                           k8s_sidecar_kube-dns-86f4d74b45-6bpkp_kube-system_199bbfb7-84eb-11e8-b409-080027090c10_0
198a4144093c        k8s.gcr.io/k8s-dns-dnsmasq-nanny-amd64     "/dnsmasq-nanny -v=2…"   32 seconds ago       Up 31 seconds                           k8s_dnsmasq_kube-dns-86f4d74b45-6bpkp_kube-system_199bbfb7-84eb-11e8-b409-080027090c10_0
ae441b5d26cd        gcr.io/k8s-minikube/storage-provisioner    "/storage-provisioner"   43 seconds ago       Up 42 seconds                           k8s_storage-provisioner_storage-provisioner_kube-system_1bb9618f-84eb-11e8-b409-080027090c10_0
551f9eb988c3        k8s.gcr.io/kubernetes-dashboard-amd64      "/dashboard --insecu…"   About a minute ago   Up About a minute                       k8s_kubernetes-dashboard_kubernetes-dashboard-5498ccf677-4grbb_kube-system_1b0efd5e-84eb-11e8-b409-080027090c10_0
5cb5f5db7d49        k8s.gcr.io/k8s-dns-kube-dns-amd64          "/kube-dns --domain=…"   2 minutes ago        Up 2 minutes                            k8s_kubedns_kube-dns-86f4d74b45-6bpkp_kube-system_199bbfb7-84eb-11e8-b409-080027090c10_0
fe57457029ac        k8s.gcr.io/kube-proxy-amd64                "/usr/local/bin/kube…"   2 minutes ago        Up 2 minutes                            k8s_kube-proxy_kube-proxy-gjc5f_kube-system_19913cb2-84eb-11e8-b409-080027090c10_0
219e836f2ebf        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 2 minutes ago        Up 2 minutes                            k8s_POD_storage-provisioner_kube-system_1bb9618f-84eb-11e8-b409-080027090c10_0
0a7c05f267c2        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 2 minutes ago        Up 2 minutes                            k8s_POD_kubernetes-dashboard-5498ccf677-4grbb_kube-system_1b0efd5e-84eb-11e8-b409-080027090c10_0
1c57d8044d78        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 2 minutes ago        Up 2 minutes                            k8s_POD_kube-dns-86f4d74b45-6bpkp_kube-system_199bbfb7-84eb-11e8-b409-080027090c10_0
0d695b8a72e8        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 2 minutes ago        Up 2 minutes                            k8s_POD_kube-proxy-gjc5f_kube-system_19913cb2-84eb-11e8-b409-080027090c10_0
9d3a011817f7        k8s.gcr.io/kube-controller-manager-amd64   "kube-controller-man…"   2 minutes ago        Up 2 minutes                            k8s_kube-controller-manager_kube-controller-manager-minikube_kube-system_0a95ac9cc2f68638674e81d2597f2a0e_0
9bdee57307e5        k8s.gcr.io/etcd-amd64                      "etcd --listen-clien…"   3 minutes ago        Up 3 minutes                            k8s_etcd_etcd-minikube_kube-system_f89d147f91330fedeca2642439aa41de_0
69826aa6e4eb        k8s.gcr.io/kube-scheduler-amd64            "kube-scheduler --ad…"   4 minutes ago        Up 4 minutes                            k8s_kube-scheduler_kube-scheduler-minikube_kube-system_31cf0ccbee286239d451edb6fb511513_0
c2e57872041d        k8s.gcr.io/kube-apiserver-amd64            "kube-apiserver --ad…"   4 minutes ago        Up 4 minutes                            k8s_kube-apiserver_kube-apiserver-minikube_kube-system_749962b3956dfb0bc6c6bb82fd6a8d5d_0
682bd7db59e3        k8s.gcr.io/kube-addon-manager              "/opt/kube-addons.sh"    5 minutes ago        Up 5 minutes                            k8s_kube-addon-manager_kube-addon-manager-minikube_kube-system_3afaf06535cc3b85be93c31632b765da_0
b5bdb3a205ba        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 6 minutes ago        Up 6 minutes                            k8s_POD_kube-controller-manager-minikube_kube-system_0a95ac9cc2f68638674e81d2597f2a0e_0
3b7beb51e3ac        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 6 minutes ago        Up 6 minutes                            k8s_POD_kube-scheduler-minikube_kube-system_31cf0ccbee286239d451edb6fb511513_0
f48d3d5ad35e        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 6 minutes ago        Up 6 minutes                            k8s_POD_etcd-minikube_kube-system_f89d147f91330fedeca2642439aa41de_0
a729eab0afbb        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 6 minutes ago        Up 6 minutes                            k8s_POD_kube-apiserver-minikube_kube-system_749962b3956dfb0bc6c6bb82fd6a8d5d_0
01247f58ae25        k8s.gcr.io/pause-amd64:3.1                 "/pause"                 6 minutes ago        Up 6 minutes                            k8s_POD_kube-addon-manager-minikube_kube-system_3afaf06535cc3b85be93c31632b765da_0
```

# 4. Set up and Run Docker Registry
```
# Setup a local Docker registry, so Kubernetes can pull the image(s) from there
$ docker run -d -p 5000:5000 --restart=always --name registry registry:2
Unable to find image 'registry:2' locally
2: Pulling from library/registry
4064ffdc82fe: Pull complete
c12c92d1c5a2: Pull complete
4fbc9b6835cc: Pull complete
765973b0f65f: Pull complete
3968771a7c3a: Pull complete
Digest: sha256:51bb55f23ef7e25ac9b8313b139a8dd45baa832943c8ad8f7da2ddad6355b3c8
Status: Downloaded newer image for registry:2
72e61e2a3e0af802e7f26f4d8f88b79fd2e0fef482d3328c1bc614672e32d9b0

# Verify Docker Registry is running
$ docker ps | grep registry
72e61e2a3e0a        registry:2      "/entrypoint.sh /etc…"   17 seconds ago      Up 16 seconds       0.0.0.0:5000->5000/tcp   registry
```

# 5. Build your Spring Boot application using fabric8-maven-plugin
Add **fabric8-maven-plugin** (https://github.com/fabric8io/fabric8-maven-plugin#usage) to the `pom.xml` of your Spring Boot application.
```
      <plugin>
        <groupId>io.fabric8</groupId>
        <artifactId>fabric8-maven-plugin</artifactId>
        <version>3.5.40</version>
      </plugin>
```

Build and deploy to Kubernetes cluster.
```
mvn clean install fabric8:resource fabric8:build fabric8:apply
```

# 6. Done!
You should see your Spring Boot application running in the Kubernetes cluster.
```
$ kubectl get pods -w
NAME                                   READY     STATUS    RESTARTS   AGE
spring-webflux-demo-54f5bb744f-xmtkm   1/1       Running   0          3m
```

# 7. Useful Notes
### Kubernetes GUI
```
minikube dashboard
```

### Delete deployment of my-app
```
mvn fabric8:resource fabric8:undeploy
```
```
kubectl delete deploy my-app
kubectl delete service my-app
```

### Clean up images in Docker
```
docker image prune -a
```

### Reset everything
```
minikube stop
minikube delete
rm -rf ~/.minikube .kube
brew uninstall kubectl
brew cask uninstall minikube
```

# References
1. https://gist.github.com/kevin-smets/b91a34cea662d0c523968472a81788f7
