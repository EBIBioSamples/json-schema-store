FROM amazoncorretto:8-al2023-jrw
EXPOSE 27017
ARG JAR_FILE=target/json-schema-store-0.0.2.jar
COPY ${JAR_FILE} json-schema-store.jar
ENTRYPOINT ["java","-jar","/json-schema-store.jar"]
