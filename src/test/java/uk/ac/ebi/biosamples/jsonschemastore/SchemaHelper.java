package uk.ac.ebi.biosamples.jsonschemastore;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoMetaSchema;

@Slf4j
public final class SchemaHelper {

  private SchemaHelper() {
  }

  public static JsonSchema getJsonSchema_test_1() {
        return new JsonSchema();
    }

    public static MetaSchema getMetaSchema_test_1() {
        return new MetaSchema();
    }

    public static MongoJsonSchema getMongoJsonSchema_test_1() {
        return new MongoJsonSchema();
    }

    public static MongoMetaSchema getMongoMetaSchema_test_1() {
        return new MongoMetaSchema();
    }

    public static JsonSchema getJsonSchema_test_2() {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchema schema = new JsonSchema();
        schema.setId("http://www.ebi.ac.uk/biosamples/schema/test_schema_2/0.0.1");
        schema.setAccession("BSDC00002");
        schema.setName("test_schema_2");
        schema.setVersion("0.0.1");
        schema.setMetaSchema("json-schema-draft-07");
        try {
            schema.setSchema(mapper.readTree("{\"k1\":\"v1\"}"));
        } catch (JsonProcessingException e) {
            log.error("Failed to map to JSON from string");
        }
        return schema;
    }

    public static MetaSchema getMetaSchema_test_2() {
        ObjectMapper mapper = new ObjectMapper();
        MetaSchema schema = new MetaSchema();
        schema.setId("http://www.ebi.ac.uk/biosamples/schema/test_schema_2/0.0.1");
        schema.setName("test_schema_2");
        schema.setVersion("0.0.1");
        schema.setMetaSchema("json-schema-draft-07");
        try {
            schema.setSchema(mapper.readTree("{\"k1\":\"v1\"}"));
        } catch (JsonProcessingException e) {
            log.error("Failed to map to JSON from string");
        }
        return schema;
    }

    public static MongoJsonSchema getMongoJsonSchema_test_2() {
        MongoJsonSchema schema = new MongoJsonSchema();
        schema.setId("http://www.ebi.ac.uk/biosamples/schema/test_schema_2/0.0.1");
        schema.setAccession("BSDC00002");
        schema.setName("test_schema_2");
        schema.setVersion("0.0.1");
        schema.setMetaSchema("json-schema-draft-07");
        schema.setSchema("{\"k1\":\"v1\"}");
        return schema;
    }

    public static MongoMetaSchema getMongoMetaSchema_test_2() {
        MongoMetaSchema schema = new MongoMetaSchema();
        schema.setId("http://www.ebi.ac.uk/biosamples/schema/test_schema_2/0.0.1");
        schema.setName("test_schema_2");
        schema.setVersion("0.0.1");
        schema.setMetaSchema("json-schema-draft-07");
        schema.setSchema("{\"k1\":\"v1\"}");
        return schema;
    }

    public static SchemaOutline getSchemaOutline_test_2() {
        SchemaOutline schema = new SchemaOutline();
        schema.setId("http://www.ebi.ac.uk/biosamples/schema/test_schema_2/0.0.1");
        schema.setAccession("BSDC00002");
        schema.setName("test_schema_2");
        schema.setVersion("0.0.1");
        return schema;
    }
}
