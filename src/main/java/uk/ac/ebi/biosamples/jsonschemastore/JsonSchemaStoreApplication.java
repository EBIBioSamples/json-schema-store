package uk.ac.ebi.biosamples.jsonschemastore;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.ac.ebi.biosamples.jsonschemastore.model.ChoiceField;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;

import java.util.List;

@Slf4j
@SpringBootApplication
public class JsonSchemaStoreApplication
        implements CommandLineRunner {

    @Autowired
    FieldRepository fieldRepository;
    public static void main(String[] args) {
        SpringApplication.run(JsonSchemaStoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ChoiceField f = ChoiceField.builder()
                .id("field1")
                .label("some label")
                .choices(List.of("opt1", "opt2"))
                .build();
        fieldRepository.save(f);
    }
}
