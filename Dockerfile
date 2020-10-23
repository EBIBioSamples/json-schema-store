FROM openjdk:11-jre
ARG JAR_FILE=storeroom/target/*.jar
COPY ${JAR_FILE} json-schema-store.jar
ENTRYPOINT ["java","-jar","/json-schema-store.jar", "--spring.profiles.active=docker"]