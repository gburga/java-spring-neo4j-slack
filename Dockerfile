FROM java:8-jdk-alpine
FROM maven:alpine
COPY . /usr/app
WORKDIR /usr/app
RUN mvn -v
RUN mvn clean install -DskipTests
WORKDIR /usr/app/target
ENTRYPOINT ["/bin/sh", "-c"]
CMD ["../wait-for.sh neo4j:7474 --timeout=60 -- java -jar slack-project-0.0.1-SNAPSHOT.jar"]