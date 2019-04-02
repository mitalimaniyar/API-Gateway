FROM maven:3-jdk-8-alpine
ADD . /API-Gateway
WORKDIR ./API-Gateway
RUN mvn clean package -DskipTests
RUN mv target/api-gateway-0.0.1-SNAPSHOT.jar target/application.jar
ENTRYPOINT [ "java", "-jar", "target/application.jar" ]
