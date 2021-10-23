



https://www.katacoda.com/scenario-examples/courses/environment-usages/minikube

# 准备image

## 生成demo工程

## Starting with Spring Initializr

https://start.spring.io/#!type=gradle-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=web

## Set up a Spring Boot Application

```java
@SpringBootApplication
@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping("/")
	public String home() {
		return "Hello Docker World";
	}
}
```

### build jar

> If you use Gradle, run the following command:
>
> ```
> ./gradlew build && java -jar build/libs/demo-0.0.1-SNAPSHOT.jar
> ```
>
> If you use Maven, run the following command:
>
> ```
> ./mvnw package && java -jar target/demo-0.0.1-SNAPSHOT.jar
> ```
>
> Then go to [localhost:8080](http://localhost:8080/) to see your “Hello Docker World” message.

## Containerize It

```bash
vim Dockerfile
```

> ```
> FROM openjdk:11
> ARG JAR_FILE=target/*.jar
> COPY ${JAR_FILE} app.jar
> ENTRYPOINT ["java","-jar","/app.jar"]
> ```

If you use Gradle, you can run it with the following command:

```
docker build --build-arg JAR_FILE=build/libs/\*.jar -t springio/gs-spring-boot-docker .
```

If you use Maven, you can run it with the following command:

```
docker build -t springio/demo-0.0.1-SNAPSHOT.jar .
```

This command builds an image and tags it as `springio/gs-spring-boot-docker`.



https://spring.io/guides/gs/spring-boot-docker/





# minikube

```bash
minikube start
$ git clone https://github.com/shawn-an/boot_demo.git

cd boot_demo/
docker build --build-arg JAR_FILE=build/libs/\*.jar -t shawn/boot-demo .

# Deploy the Application to Kubernetes
kubectl create deployment boot-demo --image=shawn/boot-demo --dry-run -o=yaml > deployment.yaml
echo --- >> deployment.yaml
kubectl create service loadbalancer boot-demo --tcp=8080:8080 --dry-run -o=yaml >> deployment.yaml
cat deployment.yaml 
```

> ``` bash
> apiVersion: apps/v1
> kind: Deployment
> metadata:
> creationTimestamp: null
> labels:
>  app: boot-demo
> name: boot-demo
> spec:
> replicas: 13
> selector:
>  matchLabels:
>    app: boot-demo
> strategy: {}
> template:
>  metadata:
>    creationTimestamp: null
>    labels:
>      app: boot-demo
>  spec:
>    containers:
>       - image: shawn/boot-demo
>         name: boot-demo
>         imagePullPolicy : Never # 使用本地的image
>         resources: {}
> status: {}
> ---
> apiVersion: v1
> kind: Service
> metadata:
>   creationTimestamp: null
>   labels:
>     app: boot-demo
>   name: boot-demo
> spec:
>   ports:
>   - name: 8080-8080
>     port: 8080
>     protocol: TCP
>     targetPort: 8080
>   selector:
>     app: boot-demo
>   type: ClusterIP
> status:
>   loadBalancer: {}
> ```
>
> 







You can take the YAML generated above and edit it if you like, or you can apply it as is:

```bash
$ kubectl apply -f deployment.yaml
deployment.apps/boot-demo created
service/boot-demo created
$ kubectl get services
NAME         TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
boot-demo    LoadBalancer   10.104.94.35   <pending>     8080:30017/TCP   61s
kubernetes   ClusterIP      10.96.0.1      <none>        443/TCP          2m34s

On cloud providers that support load balancers, an external IP address would be provisioned to access the Service. On minikube, the LoadBalancer type makes the Service accessible through the minikube service command.
$ minikube service boot-demo
|-----------|-----------|-------------|-------------------------|
| NAMESPACE |   NAME    | TARGET PORT |           URL           |
|-----------|-----------|-------------|-------------------------|
| default   | boot-demo | 8080-8080   | http://172.17.0.9:30017 |
|-----------|-----------|-------------|-------------------------|
$ curl http://172.17.0.9:30017
Hello Docker World, host name is boot-demo-6544b5946b-rftgx
$ kubectl delete service boot-demo
$ kubectl delete deployment boot-demo
```



Now you need to be able to connect to the application, which you have exposed as a Service in Kubernetes. One way to do that, which works great at development time, is to create an SSH tunnel:

```bash
$ kubectl port-forward svc/boot-demo 8080:8080 &

# Expose the Pod to the public internet using the kubectl expose command:

kubectl expose deployment boot-demo --type=LoadBalancer --port=8080
```

Then you can verify that the app is running in another terminal:

```bash
$ curl localhost:8080
{"status":"UP"}
```



```bash
 kubectl apply -f deployment.yaml
deployment.apps/demo created
$ kubectl get pods
NAME                   READY   STATUS    RESTARTS   AGE
demo-544d6cb69-7wchq   1/1     Running   0          15s
$ kubectl expose deployment demo --type=LoadBalancer --port=8080
service/demo exposed
$ kubectl get services
NAME         TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
demo         LoadBalancer   10.108.239.150   <pending>     8080:32762/TCP   18s
kubernetes   ClusterIP      10.96.0.1        <none>        443/TCP          4m23s
$ minikube service demo
|-----------|------|-------------|--------------------------|
| NAMESPACE | NAME | TARGET PORT |           URL            |
|-----------|------|-------------|--------------------------|
| default   | demo |             | http://172.17.0.11:32762 |
|-----------|------|-------------|--------------------------|
* Opening service default/demo in default browser...
Minikube Dashboard is not supported via the interactive terminal experience.

Please click the 'Preview Port 30000' link above to access the dashboard.
This will now exit. Please continue with the rest of the tutorial.
* 
X open url failed: http://172.17.0.11:32762: exit status 1
* 
* minikube is exiting due to an error. If the above message is not useful, open an issue:
  - https://github.com/kubernetes/minikube/issues/new/choose
  
$ curl http://172.17.0.11:32762
Hello Docker World
```

https://kubernetes.io/docs/tutorials/hello-minikube/





FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]













https://spring.io/guides/gs/spring-boot-kubernetes/