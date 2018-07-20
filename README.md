# akka-kubernetes

It's a simple project that explains how to configure deployments/jobs/services to perform remoting between actors running on different pods on a Kubernetes cluster.

### Implementation

* A `ClientActor` and a `ManagerActor` listen on port `6000` on their respective machines in their own pods.
* Services `client-actor` and `manager-actor` to assign and route packets to correct ports.
* Kubernetes API calls to get the service details and setup `ActorSystem` properly.
* Code and configuration for bringing it all together.

### Pre-requisites
* A Kubernetes cluster, with at least `kubectl`.
* Docker with at lease `docker`.
* SBT.

### Running the example

* `cd` to the project root.
* Run `bash deploy/build_dockerfiles.sh <your-docker-username>` from the project root. This will build the docker images and push them on the hub.
* Edit the files `job+service_k8stest-client.yaml` and `deployment+service_k8stest-manager.yaml` and replace `<your-docker-username>` with your actual username.
* Deploy the namespace with `kubectl create -f deploy/k8s/namespace_k8stest.yaml`, the manager deployment and service with `kubectl create -f deployment+service_k8stest-manager.yaml`.
* Finally, deploy the client job and service with `kubectl create -f job+service_k8stest-client.yaml`.
* Check the manager logs with `kubectl --namespace=k8stest logs <manager-pod-id>`. It should show a `"Hello"` message received from client.
