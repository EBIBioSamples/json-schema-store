package uk.ac.ebi.biosamples.jsonschemastore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;

@Slf4j
@SpringBootApplication
@EnableMongoAuditing
public class JsonSchemaStoreApplication {
    @Autowired
    FieldRepository fieldRepository;
    public static void main(String[] args) {
        SpringApplication.run(JsonSchemaStoreApplication.class, args);
    }

}
