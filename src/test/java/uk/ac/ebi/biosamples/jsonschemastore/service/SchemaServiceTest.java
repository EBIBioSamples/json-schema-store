package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.biosamples.jsonschemastore.FieldGeneratorHelper;
import uk.ac.ebi.biosamples.jsonschemastore.SchemaHelper;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldGroup;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;
import uk.ac.ebi.biosamples.jsonschemastore.util.MongoModelConverter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class SchemaServiceTest {
    @Mock
    private AccessioningService accessioningService;
    @Mock
    private SchemaRepository schemaRepository;
    @Mock
    private FieldRepository fieldRepository;
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
        when(schemaRepository.findFirstByAccessionAndVersionOrderByVersionDesc("BSDC00002", "0.0.1"))
                .thenReturn(Optional.of(mongoJsonSchemaTest));
        when(schemaRepository.save(mongoJsonSchemaTest)).thenReturn(mongoJsonSchemaTest);
        when(modelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaTest)).thenReturn(jsonSchemaTest);
        when(modelConverter.jsonSchemaToMongoJsonSchema(jsonSchemaTest)).thenReturn(mongoJsonSchemaTest);
        when(accessioningService.getSchemaAccession(jsonSchemaTest.getId())).thenReturn(jsonSchemaTest.getAccession());
    }

    @Test
    void getSchemaByIdOrLatestByAccessionShouldReturnLatestIfAccessionIsProvided() {
        when(schemaRepository.findFirstByAccessionOrderByVersionDesc("BSDC00002"))
            .thenReturn(Optional.of(SchemaHelper.getMongoJsonSchema_test_2()));
        Optional<JsonSchema> schema = schemaService.getSchemaByIdOrLatestByAccession("BSDC00002");
        assertThat(schema).isPresent();
    }

    @Test
    void getSchemaByNameAndVersion() {
        Optional<JsonSchema> schema = schemaService.getSchemaByNameAndVersion("test_schema_2", "0.0.1");
        assertTrue(schema.isPresent());
    }

    @Test
    void getSchemaByNameAndVersionShouldReturnEmptyIfNameVersionPairIsNotPresent() {
        Optional<JsonSchema> schema = schemaService.getSchemaByNameAndVersion("test_schema_fake", "0.0.1");
        assertTrue(schema.isEmpty());
    }

    @Test
    void saveSchema() {
        JsonSchema jsonSchemaTest = SchemaHelper.getJsonSchema_test_2();
        JsonSchema schema = schemaService.saveSchema(jsonSchemaTest);
        assertEquals(jsonSchemaTest, schema);
    }

    @Test
    void saveSchemaWithAccessionShouldSaveNewValueOfFieldUnits() {
        String fieldId = "test_field_id";
        Field field = FieldGeneratorHelper.getTestFieldWithIdAndUnits(fieldId, Collections.emptyList());
        Field unitModifiedField = FieldGeneratorHelper.getTestFieldWithIdAndUnits(fieldId, Collections.singletonList("m"));
        when(fieldRepository.findById(fieldId)).thenReturn(Optional.of(field));
        when(fieldRepository.save(any())).thenReturn(field);

        JsonSchema schema = SchemaHelper.getJsonSchema_test_2();
        schemaService.saveSchemaWithAccession(schema, Set.of(unitModifiedField));
        Assertions.assertThat(field.getUnits().contains("m")).isTrue();
    }

    @Test
    void extractFieldGroupsShouldExtractAllGroupsCorrectly() {
        Set<Field> fields = getTestFields();
        Set<FieldGroup> groups = schemaService.extractFieldGroups(fields);
        assertThat(groups.size()).isEqualTo(2);
    }

    @Test
    void extractFieldGroupsShouldPopulateFieldsInGroups() {
        Set<Field> fields = getTestFields();
        Set<FieldGroup> groups = schemaService.extractFieldGroups(fields);
        assertThat(
            groups.stream().findFirst().orElseThrow().getFields()
                .stream().findFirst().orElseThrow()).contains("test_field");
    }

    private static @NotNull Set<Field> getTestFields() {
        Set<Field> fields = new HashSet<>();
        Field field = new Field();
        field.setId("test_field_1");
        field.setLabel("test_field_1");
        field.setGroup("Group 1");
        fields.add(field);
        field = new Field();
        field.setId("test_field_2");
        field.setLabel("test_field_2");
        field.setGroup("Group 2");
        fields.add(field);
        return fields;
    }
}