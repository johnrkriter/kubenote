# Disclaimer
1. This guide is for running Minikube on Hyper-V. Not applicable for Virtualbox.
2. Remember to run `cmd` as administrator for all commands below.

# 1. Prerequisites

### Install Docker for Windows
https://store.docker.com/editions/community/docker-ce-desktop-windows

```
# Verify Docker
$ docker run hello-world
Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/engine/userguide/
```

### Install Chocolatey
```
$ @"%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -InputFormat None -ExecutionPolicy Bypass -Command "iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))" && SET "PATH=%PATH%;%ALLUSERSPROFILE%\chocolatey\bin"
```

### Install Kubernetes CLI
```
$ choco install kubernetes-cli
Chocolatey v0.10.11
Installing the following packages:
kubernetes-cli
By installing you accept licenses for the packages.
Progress: Downloading kubernetes-cli 1.11.0... 100%

kubernetes-cli v1.11.0 [Approved]
kubernetes-cli package files install completed. Performing other installation steps.
The package kubernetes-cli wants to run 'chocolateyInstall.ps1'.
Note: If you don't run this script, the installation will fail.
Note: To confirm automatically next time, use '-y' or consider:
choco feature enable -n allowGlobalConfirmation
Do you want to run the script?([Y]es/[N]o/[P]rint): Y

Extracting 64-bit C:\ProgramData\chocolatey\lib\kubernetes-cli\tools\kubernetes-client-windows-amd64.tar.gz to C:\ProgramData\chocolatey\lib\kubernetes-cli\tools...
C:\ProgramData\chocolatey\lib\kubernetes-cli\tools
Extracting 64-bit C:\ProgramData\chocolatey\lib\kubernetes-cli\tools\kubernetes-client-windows-amd64.tar to C:\ProgramData\chocolatey\lib\kubernetes-cli\tools...
C:\ProgramData\chocolatey\lib\kubernetes-cli\tools
 ShimGen has successfully created a shim for kubectl.exe
 The install of kubernetes-cli was successful.
  Software installed to 'C:\ProgramData\chocolatey\lib\kubernetes-cli\tools'

Chocolatey installed 1/1 packages.
 See the log for details (C:\ProgramData\chocolatey\logs\chocolatey.log).

Enjoy using Chocolatey? Explore more amazing features to take your
experience to the next level at
 https://chocolatey.org/compare
 
C:\WINDOWS\system32>kubectl version
Client Version: version.Info{Major:"1", Minor:"9", GitVersion:"v1.9.2", GitCommit:"5fa2db2bd46ac79e5e00a4e6ed24191080aa463b", GitTreeState:"clean", BuildDate:"2018-01-18T10:09:24Z", GoVersion:"go1.9.2", Compiler:"gc", Platform:"windows/amd64"}
Unable to connect to the server: dial tcp [::1]:8080: connectex: No connection could be made because the target machine actively refused it.
```

### Install Minikube
```
$ choco install minikube
```

### Verify Installs
```
$ docker --version            # Docker version 18.03.1-ce, build 9ee9f40
$ docker-compose --version    # docker-compose version 1.21.1, build 5a3f1a3
$ docker-machine --version    # docker-machine version 0.14.0, build 89b8332
$ minikube version            # minikube version: v0.28.0
$ kubectl version --client    # Client Version: version.Info{Major:"1", Minor:"9" ... "}
```

# 2. Create External Network Switch
1. Open Hyper-V Manager
2. On the right panel, click on Virtual Switch Manager
3. In Virtual Switch Manager, select "External", then "Create Virtual Switch"
4. By default, the name is set as "New Virtual Switch". This name will be used when starting Minikube in the following step.
5. Click OK.

Visual guide: https://medium.com/@JockDaRock/minikube-on-windows-10-with-hyper-v-6ef0f4dc158c

# 3. Start Minikube
```
$ minikube start --vm-driver hyperv --hyperv-virtual-switch "New Virtual Switch" --alsologtostderr
```
_Note: Append `--memory=${int}` to explicitly specify allocated memory, e.g. 1024, if you hit insufficient memory issue._

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

# 4. Use Minikube's built-in Docker Daemon
**Important!!**
This step is to ensure you are pointing to the Minikube's Docker when running any Docker command, e.g. upload/run image, etc.
Make sure you run these commands before running registry and deploying your Spring Boot app later.
```
$ minikube docker-env

SET DOCKER_TLS_VERIFY=1
SET DOCKER_HOST=tcp://192.168.0.116:2376
SET DOCKER_CERT_PATH=C:\Users\wai.loon.theng\.minikube\certs
SET DOCKER_API_VERSION=1.35
REM Run this command to configure your shell:
REM @FOR /f "tokens=*" %i IN ('minikube docker-env') DO @%i
```

```
$ @FOR /f "tokens=*" %i IN ('minikube docker-env') DO @%i
```

_Note: You can revert back to the host docker daemon by running._
```
docker-machine env -u
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

# 5. Set up and Run Docker Registry
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
$ docker ps
# You should see registry image running in your output
72e61e2a3e0a        registry:2      "/entrypoint.sh /etc…"   17 seconds ago      Up 16 seconds       0.0.0.0:5000->5000/tcp   registry
```

# 6. Build your Spring Boot application using fabric8-maven-plugin
Add **fabric8-maven-plugin** (https://github.com/fabric8io/fabric8-maven-plugin#usage) to the `pom.xml` of your Spring Boot application.
```
      <plugin>
        <groupId>io.fabric8</groupId>
        <artifactId>fabric8-maven-plugin</artifactId>
        <version>3.5.33</version>
      </plugin>
```

Build and deploy to Kubernetes cluster.
```
$ mvn clean install fabric8:resource fabric8:build fabric8:apply
```

# 7. Done!
You should see your Spring Boot application running in the Kubernetes cluster.
```
$ kubectl get pods -w
NAME                                   READY     STATUS    RESTARTS   AGE
spring-webflux-demo-54f5bb744f-xmtkm   1/1       Running   0          3m
```

# 8. Stopping Minikube
If you are using Minikube v0.28.0 (latest when this guide was written), you might fail to stop Minikube by `minikube stop`.
Instead, you can follow these steps:
1. Open Hyper-V
2. You should see minikube VM running. Double click to open VM terminal
3. Login with default username `docker` and password `tcuser`
4. Once logged in, execute `sudo poweroff`
5. Minikube should be stopped now. Verify with `minikube status`, you shall see:

```
minikube: Stopped
cluster:
kubectl:
```

## ALTERNATIVE: `minikube ssh "sudo poweroff"`

# 9. Useful Notes
### Kubernetes GUI
```
minikube dashboard
```

### Delete deployment of my-app
```
mvn fabric8:resources fabric8:undeploy
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
2. https://medium.com/@JockDaRock/minikube-on-windows-10-with-hyper-v-6ef0f4dc158c
