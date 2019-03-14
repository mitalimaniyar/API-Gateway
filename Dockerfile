FROM mitalimaniyar/openjdk-alpine-maven
RUN mvn clean
RUN mvn package -DskipTests
WORKDIR ./target
ENTRYPOINT ["java","-jar","api-gateway-0.0.1-SNAPSHOT.jar"]
