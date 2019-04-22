FROM openjdk:8-jdk-alpine
ADD /api-gateway-0.0.1-SNAPSHOT.jar /API-Gateway/application.jar
WORKDIR ./API-Gateway
ENTRYPOINT ["java", "-jar", "application.jar"]
