FROM mitalimaniyar/openjdk-alpine-maven-git
WORKDIR ./API-Gateway
RUN mvn clean
RUN mvn install -DskipTests
ENTRYPOINT [ "java", "-jar", "target/api-gateway-0.0.1-SNAPSHOT.jar" ]
