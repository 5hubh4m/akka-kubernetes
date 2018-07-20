FROM openjdk:10-jre

ADD K8STest-assembly-0.1.jar /app/

ENTRYPOINT ["java", "-cp", "/app/K8STest-assembly-0.1.jar", "k8stest.K8SClientActor"]