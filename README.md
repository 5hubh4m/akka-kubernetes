# akka-kubernetes

It's a simple project that explains how to configure deployments/jobs/services to perform remoting between actors running on different pods on a Kubernetes cluster.

### Implementation

* A `ClientActor` and a `ManagerActor` listen on port `6000` on their respective machines in their own pods.
* Services `manager-actor` to make the IP and port of the `ManagerActor` visible and accessible to `ClientActor`.
* Kubernetes API calls to get the service details and setup `ActorSystem` properly.
* Code and configuration for bringing it all together.

### Pre-requisites
* A Kubernetes cluster, with at least `kubectl`.
* Docker with at lease `docker`.
* SBT.

### Running the example

* `cd` to the project root.
* Edit files `deploy/k8s/deployment+service_k8stest-manager.yaml` and `deploy/k8s/job_k8stest-client.yaml` and replace `<your-docker-hub-username>` with your actual username. Or just use mine (`5hubh4m`) for the prebuilt docker image!
* Run `bash deploy/build_dockerfiles.sh <your-docker-hub-username>` from the project root. This will build the docker images and push them on the hub. Optionally, if you only want to run the vanilla images, skip this step and just get the docker images on my docker hub, as mentioned in the previous step.
* Deploy the namespace with `kubectl create -f deploy/k8s/namespace_k8stest.yaml`, the manager deployment and service with `kubectl create -f deployment+service_k8stest-manager.yaml`.
* Finally, deploy the client job with `kubectl create -f job_k8stest-client.yaml`.
* Check the manager logs with `kubectl --namespace=k8stest logs <manager-pod-id>`. It should show a `"Hello"` message received from client.
* Check the client logs with `kubectl --namespace=k8stest logs <client-pod-id>`. It should show a `"Success..."` on receiving an `Exit` message from manager.
