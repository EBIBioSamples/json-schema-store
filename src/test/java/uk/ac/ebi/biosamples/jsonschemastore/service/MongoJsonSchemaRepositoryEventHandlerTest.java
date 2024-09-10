package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

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
        Assertions.assertThat(schema.getVersion()).isEqualTo("1.0.6");
        Assertions.assertThat(schema.getId()).isEqualTo("test01:1.0.6");
    }

    @Test
    void initialVersionAndAccessionAssignedCorrectly() {
        // Given
        MongoJsonSchema schema = new MongoJsonSchema();
        schema.setTitle("test 01");

        // When
        eventHandler.handleBeforeCreate(schema);  // Trigger the event

        // Then
        Assertions.assertThat(schema.getVersion()).isEqualTo("1.0.0");
        Assertions.assertThat(schema.getId()).isEqualTo("test_01:1.0.0");
        Assertions.assertThat(schema.getName()).isEqualTo("test_01");
    }



}
