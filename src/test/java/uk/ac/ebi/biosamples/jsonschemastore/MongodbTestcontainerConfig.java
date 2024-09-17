package uk.ac.ebi.biosamples.jsonschemastore;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

@Configuration
@EnableMongoRepositories
public class MongodbTestcontainerConfig {
  @Container
  public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
      .withExposedPorts(27017);

  static {
    mongoDBContainer.start();
    var mappedPort = mongoDBContainer.getMappedPort(27017);
    System.setProperty("spring.data.mongodb.port", String.valueOf(mappedPort));
  }
}
