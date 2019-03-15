FROM mitalimaniyar/api-gateway
RUN git clone https://github.com/mitalimaniyar/API-Gateway
RUN mvn clean
RUN mvn package -DskipTests
WORKDIR ./target
RUN ls
ENTRYPOINT [ "java", "-jar", "api-gateway-0.0.1-SNAPSHOT.jar" ]
