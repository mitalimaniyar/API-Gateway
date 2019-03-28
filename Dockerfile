FROM maven:3-jdk-8-alpine
RUN git clone https://github.com/mitalimaniyar/API-Gateway
WORKDIR ./API-Gateway
RUN mvn clean
RUN mvn install -DskipTests
ENTRYPOINT [ "java", "-jar", "target/api-gateway-0.0.1-SNAPSHOT.jar" ]
