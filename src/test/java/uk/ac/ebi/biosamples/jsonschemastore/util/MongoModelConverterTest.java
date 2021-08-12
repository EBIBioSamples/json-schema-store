package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.biosamples.jsonschemastore.SchemaHelper;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoMetaSchema;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MongoModelConverterTest {
    MongoModelConverter mongoModelConverter = new MongoModelConverter(new ObjectMapper());

    @Test
    void jsonSchemaToMongoJsonSchema() {
        JsonSchema jsonSchemaTest = SchemaHelper.getJsonSchema_test_2();
        MongoJsonSchema mongoJsonSchemaExpected = SchemaHelper.getMongoJsonSchema_test_2();
        MongoJsonSchema mongoJsonSchema = mongoModelConverter.jsonSchemaToMongoJsonSchema(jsonSchemaTest);
        assertEquals(mongoJsonSchemaExpected, mongoJsonSchema);
    }

    @Test
    void mongoJsonSchemaToJsonSchema() {
        MongoJsonSchema mongoJsonSchemaTest = SchemaHelper.getMongoJsonSchema_test_2();
        JsonSchema jsonSchemaExpected = SchemaHelper.getJsonSchema_test_2();
        JsonSchema jsonSchema = mongoModelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaTest);
        assertEquals(jsonSchemaExpected, jsonSchema);
    }

    @Test
    void metaSchemaToMongoMetaSchema() {
        MetaSchema schemaTest = SchemaHelper.getMetaSchema_test_2();
        MongoMetaSchema mongoSchemaExpected = SchemaHelper.getMongoMetaSchema_test_2();
        MongoMetaSchema mongoSchema = mongoModelConverter.metaSchemaToMongoMetaSchema(schemaTest);
        assertEquals(mongoSchemaExpected, mongoSchema);
    }

    @Test
    void mongoMetaSchemaToMetaSchema() {
        MongoMetaSchema mongoSchemaTest = SchemaHelper.getMongoMetaSchema_test_2();
        MetaSchema schemaExpected = SchemaHelper.getMetaSchema_test_2();
        MetaSchema schema = mongoModelConverter.mongoMetaSchemaToMetaSchema(mongoSchemaTest);
        assertEquals(schemaExpected, schema);
    }

    @Test
    void mongoJsonSchemaToSchemaOutline() {
        MongoJsonSchema mongoJsonSchemaTest = SchemaHelper.getMongoJsonSchema_test_2();
        SchemaOutline schemaExpected = SchemaHelper.getSchemaOutline_test_2();
        SchemaOutline schema = mongoModelConverter.mongoJsonSchemaToSchemaOutline(mongoJsonSchemaTest);
        assertEquals(schemaExpected, schema);
    }
}