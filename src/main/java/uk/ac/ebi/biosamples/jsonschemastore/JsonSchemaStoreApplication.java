package uk.ac.ebi.biosamples.jsonschemastore;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.biosamples.jsonschemastore.client.ValidatorClient;

@SpringBootApplication
public class JsonSchemaStoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(JsonSchemaStoreApplication.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public ValidatorClient validatorClient() {
    return new ValidatorClient();
  }
}
