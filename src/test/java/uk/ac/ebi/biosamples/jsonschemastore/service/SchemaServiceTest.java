package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.biosamples.jsonschemastore.SchemaHelper;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;
import uk.ac.ebi.biosamples.jsonschemastore.util.MongoModelConverter;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class SchemaServiceTest {
    @Mock
    private AccessioningService accessioningService;
    @Mock
    private SchemaRepository schemaRepository;
    @Mock
    private MongoModelConverter modelConverter;
    @InjectMocks
    private SchemaService schemaService;

    @BeforeEach
    void setup() {
        MongoJsonSchema mongoJsonSchemaTest = SchemaHelper.getMongoJsonSchema_test_2();
        JsonSchema jsonSchemaTest = SchemaHelper.getJsonSchema_test_2();
        when(schemaRepository.findFirstByNameAndVersionOrderByVersionDesc("test_schema_2", "0.0.1"))
                .thenReturn(Optional.of(mongoJsonSchemaTest));
        when(schemaRepository.findFirstByAccessionAndVersionOrderByVersionDesc("ERC100002", "0.0.1"))
                .thenReturn(Optional.of(mongoJsonSchemaTest));
        when(schemaRepository.save(mongoJsonSchemaTest)).thenReturn(mongoJsonSchemaTest);
        when(modelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaTest)).thenReturn(jsonSchemaTest);
        when(modelConverter.jsonSchemaToMongoJsonSchema(jsonSchemaTest)).thenReturn(mongoJsonSchemaTest);
        when(accessioningService.getSchemaAccession(jsonSchemaTest.getId())).thenReturn(jsonSchemaTest.getAccession());
    }

    @Test
    void getSchemaByNameAndVersion() {
        Optional<JsonSchema> schema = schemaService.getSchemaByNameAndVersion("test_schema_2", "0.0.1");
        assertTrue(schema.isPresent());
    }

    @Test
    void getSchemaByNameAndVersion_schema_not_present() {

        Optional<JsonSchema> schema = schemaService.getSchemaByNameAndVersion("test_schema_fake", "0.0.1");
        assertTrue(schema.isEmpty());
    }

    @Test
    void saveSchema() {
        JsonSchema jsonSchemaTest = SchemaHelper.getJsonSchema_test_2();
        JsonSchema schema = schemaService.saveSchema(jsonSchemaTest);
        assertEquals(jsonSchemaTest, schema);
    }
}