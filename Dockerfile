FROM eclipse-temurin:21-jdk-alpine

VOLUME /tmp
EXPOSE 8084

COPY target/WellnessHub-Backend-0.0.1-SNAPSHOT.jar WellnessHubBackend.jar

ENTRYPOINT ["java", "-jar", "/WellnessHubBackend.jar"]