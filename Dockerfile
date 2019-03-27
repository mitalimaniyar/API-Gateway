FROM mitalimaniyar/openjdk-alpine-maven-git
RUN git clone https://github.com/mitalimaniyar/API-Gateway 
WORKDIR ./API-Gateway
RUN mvn clean
RUN mvn package -DskipTests
