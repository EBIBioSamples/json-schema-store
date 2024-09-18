package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.assertj.core.api.Condition;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class MongoJsonSchemaRepositoryEventHandlerTest {
    @Mock
    private FieldRepository fieldRepository;
    @Mock
    private SchemaRepository schemaRepository;

    @InjectMocks
    private MongoJsonSchemaRepositoryEventHandler eventHandler; // The event handler you're testing

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        when(schemaRepository.count()).thenReturn(15L);
    }
    @Test
    void handleBeforeSave() {
        // Given
        MongoJsonSchema schema = new MongoJsonSchema();
        schema.setVersion("1.0.5");
        schema.setName("test01");

        // When
        eventHandler.handleBeforeSave(schema);  // Trigger the event

        // Then
        assertThat(schema)
                .hasFieldOrPropertyWithValue("version","1.0.6")
                .hasFieldOrPropertyWithValue("id","test01:1.0.6")
                .hasFieldOrPropertyWithValue("editable",true);

    }

    @Test
    void initialVersionAndAccessionAssignedCorrectly() {
        // Given
        MongoJsonSchema schema = new MongoJsonSchema();
        schema.setTitle("test 01");

        // when
        eventHandler.handleBeforeCreate(schema);

        // Then
        assertThat(schema)
                .hasFieldOrPropertyWithValue("version","1.0.6")
                .hasFieldOrPropertyWithValue("id","test01:1.0.6")
                .hasFieldOrPropertyWithValue("editable",true)
                .hasFieldOrPropertyWithValue("name","test_01");
    }

    @Test
    void addingAFieldToChecklistUpdatesRefOnField() {
        // Given a checklist pointing to 2 fields
        MongoJsonSchema schema = SchemaBuilder.newSchema();
        Field f1 = SchemaBuilder.addFieldWithId(schema, "f1");

        when(fieldRepository.findById("f1")).thenReturn(Optional.of(f1));

        // when
        eventHandler.handleBeforeCreate(schema);
        eventHandler.handleBeforeSave(schema);
        eventHandler.handleAfterCreateOrSave(schema);

        // then
        assertThat(fieldRepository.findById("f1").get().getUsedBySchemas())
                .contains("test_01:1.0.1");

    }

    @Test
    void removingAFieldFromASchemaShouldRemoveRef() {
        // Given a checklist pointing to 2 fields
        MongoJsonSchema schema = SchemaBuilder.newSchema();
        Field f1 = SchemaBuilder.addFieldWithId(schema, "f1");
        Field f2 = SchemaBuilder.addFieldWithId(schema, "f2");

        when(fieldRepository.findById("f1")).thenReturn(Optional.of(f1));
        when(fieldRepository.findById("f2")).thenReturn(Optional.of(f2));
        eventHandler.handleBeforeCreate(schema);

        // when
        schema.getSchemaFieldAssociations().removeIf(x->x.getFieldId().equals("f2"));
        eventHandler.handleBeforeSave(schema);
        eventHandler.handleAfterCreateOrSave(schema);

        // then
        assertThat(fieldRepository.findById("f2").get().getUsedBySchemas())
                .doesNotContain("test_01:1.0.1");
        assertThat(fieldRepository.findById("f1").get().getUsedBySchemas())
                .contains("test_01:1.0.1");
    }


    public static class SchemaBuilder {

        @NotNull
        public static MongoJsonSchema newSchema() {
            MongoJsonSchema schema = new MongoJsonSchema();
            schema.setTitle("test 01");
            return schema;
        }

        @NotNull
        public static Field addFieldWithId(MongoJsonSchema schema, String fieldId) {
            Field field = new Field();
            field.setId(fieldId);
            SchemaFieldAssociation association = new SchemaFieldAssociation();
            association.setFieldId(fieldId);
            schema.getSchemaFieldAssociations().add(association);
            return field;
        }
    }
}
