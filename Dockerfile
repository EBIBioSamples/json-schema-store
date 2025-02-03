FROM maven:3.9.9-amazoncorretto-21 as builder
# Set working directory
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src/ ./src/

# Define Maven CLI options
ENV MAVEN_CLI_OPTS="-DskipTests"

# Build the project
RUN mvn $MAVEN_CLI_OPTS clean package


FROM amazoncorretto:21-alpine

WORKDIR /app
EXPOSE 27017
ARG JAR_FILE=/app/target/json-schema-store-0.0.2.jar
COPY --from=builder ${JAR_FILE} json-schema-store.jar
ENTRYPOINT ["java","-jar","/app/json-schema-store.jar"]
