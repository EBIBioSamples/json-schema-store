package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.Multiplicity;
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

    @Mock AccessioningService accessioningService;
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
        schema.setVersion("1.0");
        schema.setName("test_01");

        // When
        eventHandler.handleBeforeSave(schema);  // Trigger the event

        // Then
        assertThat(schema)
                .hasFieldOrPropertyWithValue("version","1.1")
                .hasFieldOrPropertyWithValue("id","test_01:1.1")
                .hasFieldOrPropertyWithValue("editable",true)
                .hasFieldOrPropertyWithValue("latest",true);

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
                .hasFieldOrPropertyWithValue("version","1.0")
                .hasFieldOrPropertyWithValue("id","test_01:1.0")
                .hasFieldOrPropertyWithValue("editable",true)
                .hasFieldOrPropertyWithValue("latest",true)
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
                .contains("test_01:1.1");

    }

    @Test
    void removingAFieldFromASchemaShouldRemoveRef() {
        // Given a checklist pointing to 2 fields
        MongoJsonSchema schema = SchemaBuilder.newSchema();
        Field f1 = SchemaBuilder.addFieldWithId(schema, "f1");
        Field f2 = SchemaBuilder.addFieldWithId(schema, "f2_to_remove");

        when(fieldRepository.findById("f1")).thenReturn(Optional.of(f1));
        when(fieldRepository.findById(f2.getId())).thenReturn(Optional.of(f2));
        eventHandler.handleBeforeCreate(schema);

        // when
        schema.getSchemaFieldAssociations().removeIf(x->x.getFieldId().equals("f2_to_remove"));
        eventHandler.handleBeforeSave(schema);
        eventHandler.handleAfterCreateOrSave(schema);

        // then
        assertThat(fieldRepository.findById(f2.getId()).get().getUsedBySchemas())
                .doesNotContain("test_01:1.1");
        assertThat(fieldRepository.findById(f1.getId()).get().getUsedBySchemas())
                .contains("test_01:1.1");
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
            SchemaFieldAssociation association = new SchemaFieldAssociation(fieldId, Property.RequirementType.MANDATORY, Multiplicity.Single);
            schema.getSchemaFieldAssociations().add(association);
            return field;
        }
    }
}
