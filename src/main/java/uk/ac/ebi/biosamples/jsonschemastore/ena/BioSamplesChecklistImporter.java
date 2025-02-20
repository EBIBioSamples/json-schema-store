package uk.ac.ebi.biosamples.jsonschemastore.ena;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class BioSamplesChecklistImporter {
  private static final String BIOSAMPLES_SCHEMA_FILE = "env/biosamples_schema.json";
  private final ObjectMapper objectMapper;
  private final SchemaRepository schemaRepository;

  public BioSamplesChecklistImporter(ObjectMapper objectMapper, SchemaRepository schemaRepository) {
    this.objectMapper = objectMapper;
    this.schemaRepository = schemaRepository;
  }

  public void importBioSamplesSchemaFromFileToDb() {
    try {
      List<MongoJsonSchema> schemas = readSchemaFromFile();
      persistBioSamplesSchema(schemas);
      log.error("Biosamples schema loaded to the database successfully");
    } catch (IOException e) {
      log.error("Failed to load biosampoles schema from: " + BIOSAMPLES_SCHEMA_FILE);
      throw new RuntimeException("Failed to load biosamples schema", e);
    }

  }

  List<MongoJsonSchema> readSchemaFromFile() throws IOException {
    return objectMapper.readValue(getClass().getClassLoader()
        .getResource(BIOSAMPLES_SCHEMA_FILE), new TypeReference<List<MongoJsonSchema>>() {
    });
  }

  private void persistBioSamplesSchema(List<MongoJsonSchema> schemas) {
    schemaRepository.saveAll(schemas);
  }
}
