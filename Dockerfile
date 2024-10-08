FROM openjdk:17-jdk-slim
EXPOSE 27017
ARG JAR_FILE=target/json-schema-store-0.0.2.jar
COPY ${JAR_FILE} json-schema-store.jar
ENTRYPOINT ["java","-jar","/json-schema-store.jar"]
