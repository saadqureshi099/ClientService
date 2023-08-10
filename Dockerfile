FROM openjdk:17
EXPOSE 8088
COPY target/client-service-1.0-SNAPSHOT.jar client-service-docker.jar

ENTRYPOINT ["java", "-jar","/client-service-docker.jar"]