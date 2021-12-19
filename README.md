## What is Kubernetes?

Kubernetes is a portable, extensible, open-source platform for managing containerized workloads and services, that facilitates both **declarative configuration and automation**. We can cluster multiple servers into a K8s cluster and then run the command to apply the YAML configuration file, which will automatically deploy our services to available servers.

The name Kubernetes originates from Greek, meaning helmsman or pilot. **K8s** as an abbreviation results from counting the eight letters between the "K" and the "s". 

Deploying applications has gone through three eras.

![img](https://d33wubrfki0l68.cloudfront.net/26a177ede4d7b032362289c6fccd448fc4a91174/eb693/images/docs/container_evolution.svg)

**Traditional deployment era:** Early on, organizations ran applications on physical servers. There was no way to define resource boundaries for applications in a physical server, and this caused resource allocation issues. For example, if multiple applications run on a physical server, there can be instances where one application would take up most of the resources, and as a result, the other applications would underperform. A solution for this would be to run each application on a different physical server. But this did not scale as resources were underutilized, and it was expensive for organizations to maintain many physical servers.

**Virtualized deployment era:** As a solution, virtualization was introduced. It allows you to run multiple Virtual Machines (VMs) on a single physical server's CPU. Virtualization allows applications to be isolated between VMs and provides a level of security as the information of one application cannot be freely accessed by another application. Virtualization allows better utilization of resources in a physical server and allows better scalability because an application can be added or updated easily, reduces hardware costs, and much more. With virtualization you can present a set of physical resources as a cluster of disposable virtual machines. **Each VM is a full machine running all the components, including its own operating system, on top of the virtualized hardware.**

**Container deployment era:** Containers are similar to VMs, but they have relaxed isolation properties to share the Operating System (OS) among the applications. Therefore, containers are considered lightweight. Similar to a VM, a container has its own filesystem, share of CPU, memory, process space, and more. As they are decoupled from the underlying infrastructure, they are portable across clouds and OS distributions.



## Why you need Kubernetes and what it can do

Containers are a good way to bundle and run your applications. In a production environment, you need to manage the containers that run the applications and ensure that there is no downtime. For example, if a container goes down, another container needs to start. Wouldn't it be easier if this behavior was handled by a system?

Kubernetes provides you with a framework to run distributed systems resiliently. It takes care of scaling and failover for your application, provides deployment patterns, and more.

For example, Kubernetes can easily manage a canary deployment for your system. And Kubernetes is able to load balance and distribute the network traffic so that the deployment is stable.

## Kubernetes Components

A Kubernetes cluster consists of a set of worker machines, called [nodes](https://kubernetes.io/docs/concepts/architecture/nodes/), that run containerized applications. Every cluster has at least one worker node.

The worker node(s) host the [Pods](https://kubernetes.io/docs/concepts/workloads/pods/) that are the components of the application workload. The [control plane](https://kubernetes.io/docs/reference/glossary/?all=true#term-control-plane) manages the worker nodes and the Pods in the cluster. In production environments, the control plane usually runs across multiple computers and a cluster usually runs multiple nodes, providing fault-tolerance and high availability.

![img](https://d33wubrfki0l68.cloudfront.net/2475489eaf20163ec0f54ddc1d92aa8d4c87c96b/e7c81/images/docs/components-of-kubernetes.svg)

### Pods

*Pods* are the smallest deployable units of computing that you can create and manage in Kubernetes. **A Pod is a group of one or more [containers](https://kubernetes.io/docs/concepts/containers/)**, with shared storage and network resources, and a specification for how to run the containers. 

#### Using Pods

K8s is based on YAML configuration file, The following is an example of a Pod which consists of a container running the image `nginx:1.14.2`.

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx
spec:
   containers:
   - name: nginx
     image: nginx:1.14.2
     imagePullPolicy : Never # use the loal image     
     ports:
     - containerPort: 80
   restartPolicy: Never
```

- metadata:

   Standard object's metadata. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#metadata

  - namespace: a namespace is a DNS compatible label that objects are subdivided into. The default namespace is 'default'. 
  - name: a string that uniquely identifies this object within the current namespace (see [the identifiers docs](https://kubernetes.io/docs/user-guide/identifiers/)). This value is used in the path when retrieving an individual object.

- spec : Specification of the desired behavior of the pod. More info: https://git.k8s.io/community/contributors/devel/sig-architecture/api-conventions.md#spec-and-status

- containers : List of containers belonging to the pod. Containers cannot currently be added or removed. There must be at least one container in a Pod. Cannot be updated.

  - name : Name of the container specified as a DNS_LABEL. Each container in a pod must have a unique name (DNS_LABEL). Cannot be updated
  - image : Docker image name.
  - imagePullPolicy : Image pull policy. One of Always, Never, IfNotPresent. Defaults to Always if :latest tag is specified, or IfNotPresent otherwise
  - ports : List of ports to expose from the container.

- restartPolicy: restart policy for all containers within the pod. One of Always, OnFailure, Never. Default to Always



1. To create the Pod shown above, run the following command:

   ```bash
   $ kubectl apply -f https://k8s.io/examples/pods/simple-pod.yaml
   # or
   $ kubectl apply -f ./nginx.yaml
   ```

2. Run the following command to check this pod status

   ```bash
   $ kubectl get pods
    
   NAME                         READY   STATUS    RESTARTS   AGE
   boot-demo-6544b5946b-g66x8   1/1     Running   0          2m41s
   ```

   Notes: 

   Pods in a Kubernetes cluster are used in two main ways:

   - **Pods that run a single container**. The "one-container-per-Pod" model is the most common Kubernetes use case; in this case, you can think of a Pod as a wrapper around a single container; Kubernetes manages Pods rather than managing the containers directly.
   - **Pods that run multiple containers that need to work together**. A Pod can encapsulate an application composed of multiple co-located containers that are tightly coupled and need to share resources. These co-located containers form a single cohesive unit of service—for example, one container serving data stored in a shared volume to the public, while a separate *sidecar* container refreshes or updates those files. The Pod wraps these containers, storage resources, and an ephemeral network identity together as a single unit.

   Each Pod is meant to run a single instance of a given application. If you want to scale your application horizontally (to provide more overall resources by running more instances), you should use multiple Pods, one for each instance. In Kubernetes, this is typically referred to as *replication*. Replicated Pods are usually created and managed as a group by a workload resource and its [controller](https://kubernetes.io/docs/concepts/architecture/controller/).

#### Pod templates

PodTemplates are specifications for creating Pods, we can create multiple pods through the template.

The sample below is a manifest for a simple Job with a `template` that starts one container. The container in that Pod prints a message then pauses.

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: hello
spec:
  template:
    # This is the pod template
    spec:
      containers:
      - name: hello
        image: busybox
        command: ['sh', '-c', 'echo "Hello, Kubernetes!" && sleep 3600']
      restartPolicy: OnFailure
    # The pod template ends here
```

## Deployment

Usually, we don't need to create Pods directly, even singleton Pods. If we have three pods running, then one of them failed unexpectedly, we should manually create a new pod to recover. A workload is an application running on Kubernetes, it helps us manage a set of pods. There are other workload resources, such as StatefulSet, DaemonSet Job, and CronJob. In this blog, let's focus on deployment. More info: https://kubernetes.io/docs/concepts/workloads/controllers/

The following is an example of deployment that consists of 3 replicas

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
        - containerPort: 80
```

- labels: a map of string keys and values that can be used to organize and categorize objects
- replicas: Number of desired pods. This is a pointer to distinguish between explicit zero and not specified. Defaults to 1.
- selector: Label selector for pods. Existing ReplicaSets whose pods are selected by this will be the ones affected by this deployment. It must match the pod template's labels.

In this example:

- A Deployment named `nginx-deployment` is created, indicated by the `.metadata.name` field.
- The Deployment creates three replicated Pods, indicated by the `.spec.replicas` field.
- The `.spec.selector` field defines how the Deployment finds which Pods to manage. In this case, you select a label that is defined in the Pod template (`app: nginx`). However, more sophisticated selection rules are possible, as long as the Pod template itself satisfies the rule.

The `template` field contains the following sub-fields:

- The Pods are labeled `app: nginx `using the `.metadata.labels` field.
- The Pod template's specification, or `.template.spec` field, indicates that the Pods run one container, `nginx`, which runs the `nginx` [Docker Hub](https://hub.docker.com/) image at version 1.14.2.
- Create one container and name it `nginx` using the `.spec.template.spec.containers[0].name` field.

### Using Deployments

1. Run the following command to create the above Deployment:

   ```bash
   # Create the Deployment by running the following command:
   $ kubectl apply -f https:``//k8s``.io``/examples/controllers/nginx-deployment``.yaml
   ```

2. Run the following command to check if the Deployment was created.

   ```bash
   $ kubectl get deployments
    
   NAME               READY   UP-TO-DATE   AVAILABLE   AGE
   nginx-deployment   0/3     0            0           1s
   ```
   When you inspect the Deployments in your cluster, the following fields are displayed:
   
     - `NAME` lists the names of the Deployments in the namespace.
   
     - `READY` displays how many replicas of the application are available to your users. It follows the pattern ready/desired.
   
     - `UP-TO-DATE` displays the number of replicas that have been updated to achieve the desired state.
   
     - `AVAILABLE` displays how many replicas of the application are available to your users.
   
     - `AGE` displays the amount of time that the application has been running.
   
3. Run the command again a few seconds later. The output is similar to this:

   ```bash
   $ kubectl get deployments
    
   NAME               READY   UP-TO-DATE   AVAILABLE   AGE
   nginx-deployment   3/3     3            3           18s
   ```
   Notice that the Deployment has created all three replicas, and all replicas are up-to-date (they contain the latest Pod template) and available.

4. To see the ReplicaSet (`rs`) created by the Deployment, run `kubectl get rs`. The output is similar to this:

   ```bash
   $ kubectl get rs
    
   NAME                          DESIRED   CURRENT   READY   AGE
   nginx-deployment-75675f5897   3         3         3       18s
   ```

5.  ReplicaSet output shows the following fields:

   - `NAME` lists the names of the ReplicaSets in the namespace.
   - `DESIRED` displays the desired number of *replicas* of the application, which you define when you create the Deployment. This is the *desired state*.
   - `CURRENT` displays how many replicas are currently running.
   - `READY` displays how many replicas of the application are available to your users.
   - `AGE` displays the amount of time that the application has been running.

   Notice that the name of the ReplicaSet is always formatted as `[DEPLOYMENT-NAME]-[RANDOM-STRING]`. The random string is randomly generated and uses the `pod-template-hash` as a seed.

6. To see the labels automatically generated for each Pod, run the following command

   ```bash
   $ kubectl get pods --show-labels
    
   NAME                                READY     STATUS    RESTARTS   AGE       LABELS
   nginx-deployment-75675f5897-7ci7o   1/1       Running   0          18s       app=nginx,pod-template-hash=3123191453
   nginx-deployment-75675f5897-kzszj   1/1       Running   0          18s       app=nginx,pod-template-hash=3123191453
   nginx-deployment-75675f5897-qqcnn   1/1       Running   0          18s       app=nginx,pod-template-hash=3123191453
   ```

   







# Demo

Now we can create a demo based on what we've learned so far. Suppose we need to create an API service and 3 replicas, and when some requests are received, we need to distribute the request to a different replica. 

In this demo, we will use an [online K8s environment](https://www.katacoda.com/scenario-examples/courses/environment-usages/minikube) that can be used directly without installation. It is based on a minikube.

**Minikube** is local Kubernetes, focusing on making it easy to learn and develop for Kubernetes.

## Create the API service

1. Generate a web project based on Spring Initializr and download it

   [https://start.spring.io/#!type=gradle-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=web

2. Use your favorite editor, edit the DemoApplaction.java file to add a controller to receive the request, and return the hostname

   ```java
   @SpringBootApplication
   @RestController
   public class DemoApplication {
    
       public static void main(String[] args) {
           SpringApplication.run(DemoApplication.class, args);
       }
    
       @RequestMapping("/")
       public String home() {  
           return "Hello K8s, host name is "+Inet4Address.getLocalHost().getHostName();  
       }
   }
   ```

3. Create a Dockerfile, it is used when creating images on the server

   ```dockerfile
   FROM openjdk:11
   ARG JAR_FILE=target/*.jar
   COPY ${JAR_FILE} app.jar
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

4. Build the jar file 

5. Upload the project to your GitHub or any other remote address





## Deploy the API service

1. Open the online Kubernetes environment

   https://www.katacoda.com/scenario-examples/courses/environment-usages/minikube

2. Execute command "minikube start" to start minikube

3. Download the jar file from your remote address

   ```bash
   $ git clone https://github.com/shawn-an/boot_demo.git
   ```

4. Build a docker image according to the Dockerfile. Usually, we need to upload the image to the google registry or other registries, then the K8s will automatically download it from the registry.

   ```bash
   $ cd boot_demo/
   $ docker build --build-arg JAR_FILE=build/libs/\*.jar -t shawn/boot-demo .
   ```

5. Generate the deployment YAML file

   ```bash
   # create a deplyment template
   $ kubectl create deployment boot-demo --image=shawn/boot-demo --dry-run -o=yaml > deployment.yaml
    
   $ echo --- >> deployment.yaml
    
   # create a service with loadbalancer template
   $ kubectl create service loadbalancer boot-demo --tcp=8080:8080 --dry-run -o=yaml >> deployment.yaml
    
   # Now let's take a look at the YAML file
   $ cat deployment.yaml
   
   ```

6. At this point we are almost done with the configuration file, but we need to change a few custom settings. 

   Type "vim deployment.yaml" to edit the YAML file

   - Change the number of the replicas to 3
   - Add a field "imagePullPolicy : Never", which means K8s will not download the image from Google Registry. Since we have already built the image on this machine.

   ```bash
   apiVersion: apps/v1
   kind: Deployment
   metadata:
   creationTimestamp: null
   labels:
    app: boot-demo
   name: boot-demo
   spec:
   replicas: 3 # change the replica number to 3
   selector:
    matchLabels:
      app: boot-demo
   strategy: {}
   template:
    metadata:
      creationTimestamp: null
      labels:
        app: boot-demo
    spec:
      containers:
         - image: shawn/boot-demo
           name: boot-demo
           imagePullPolicy : Never # use the loal image
           resources: {}
   status: {}
   ---
   apiVersion: v1
   kind: Service
   metadata:
     creationTimestamp: null
     labels:
       app: boot-demo
     name: boot-demo
   spec:
     ports:
     - name: 8080-8080
       port: 8080
       protocol: TCP
       targetPort: 8080
     selector:
       app: boot-demo
     type: ClusterIP
   status:
     loadBalancer: {}
   ```

7. Then apply the deployment file, and look what happened

   ```bash
   $ kubectl apply -f deployment.yaml
   deployment.apps/boot-demo created
   service/boot-demo created
   ```

   

8. Now, let's check our pods and service status

   ```bash
   # check pods status
   $ kubectl get pods
   NAME                         READY   STATUS    RESTARTS   AGE
   boot-demo-6544b5946b-g66x8   1/1     Running   0          2m41s
   boot-demo-6544b5946b-jh2fc   1/1     Running   0          2m41s
   boot-demo-6544b5946b-k8bs4   1/1     Running   0          2m41s
    
   # check service status
   $ kubectl get services
   NAME         TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
   boot-demo    LoadBalancer   10.110.132.65   <pending>     8080:31097/TCP   34s
   kubernetes   ClusterIP      10.96.0.1       <none>        443/TCP          31m
   ```

   

9. Expose the service. On cloud providers that support load balancers, an external IP address would be provisioned to access the Service. On minikube, the LoadBalancer type makes the Service accessible through the minikube service command.

   ```bash
   $ minikube service boot-demo
   |-----------|-----------|-------------|--------------------------|
   | NAMESPACE |   NAME    | TARGET PORT |           URL            |
   |-----------|-----------|-------------|--------------------------|
   | default   | boot-demo | 8080-8080   | http://172.17.0.18:31097 |
   |-----------|-----------|-------------|--------------------------|
   * Opening service default/boot-demo in default browser...
   Minikube Dashboard is not supported via the interactive terminal experience.
    
   Please click the 'Preview Port 30000' link above to access the dashboard.
   This will now exit. Please continue with the rest of the tutorial.
   *
   X open url failed: http://172.17.0.18:31097: exit status 1
   *
   * minikube is exiting due to an error. If the above message is not useful, open an issue:
     - https://github.com/kubernetes/minikube/issues/new/choose
   ```

10. Now, let's verify the service is available and the load balancer is working. 

    ```bash
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-k8bs4
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-g66x8
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-k8bs4
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-k8bs4
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-g66x8
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-k8bs4
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-jh2fc
    $ curl http://172.17.0.18:31097
    Hello K8s, host name is boot-demo-6544b5946b-jh2fc
    ```

    


