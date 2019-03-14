FROM mitalimaniyar/api-gateway
RUN git clone https://github.com/mitalimaniyar/API-Gateway
WORKDIR ./API-Gateway/target
RUN rm docker-spring-boot.jar docker-spring-boot.jar.original
WORKDIR ../
RUN mvn package -DskipTests
WORKDIR ./target
RUN ls
ENTRYPOINT [ "java", "-jar", "api-gateway-0.0.1-SNAPSHOT.jar" ]
