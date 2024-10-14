package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.Multiplicity;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class FieldRepositoryEventHandlerTest {
  @Mock
  FieldRepository fieldRepository;
  @Mock
  SchemaRepository schemaRepository;

  @InjectMocks
  private FieldRepositoryEventHandler fieldRepositoryEventHandler;

  private static Field getTestField() {
    Field field = new Field();
    field.setLabel("address");
    field.setName("handleBeforeSaveField");
    field.setVersion("1.0");
    field.setId("handleBeforeSaveField:1.0");
    field.setUsedBySchemas(Set.of("BSDC00001:1.0", "BSDC00002:1.0"));
    return field;
  }

  private static MongoJsonSchema getTestSchema(String accession) {
    MongoJsonSchema schema = new MongoJsonSchema();
    schema.setAccession(accession);
    schema.setVersion("1.0");
    schema.setId(accession + ":1.0");
    schema.setTitle("Test schema");

    SchemaFieldAssociation field = new SchemaFieldAssociation();
    field.setFieldId("handleBeforeSaveField:1.0");
    field.setCardinality(Property.AttributeCardinality.MANDATORY);
    field.setMultiplicity(Multiplicity.Single);
    schema.setSchemaFieldAssociations(Collections.singletonList(field));

    return schema;
  }

  @BeforeEach
  void setup() {
    Field field = getTestField();
    when(fieldRepository.findById(field.getId())).thenReturn(Optional.of(getTestField()));
    when(schemaRepository.findById("BSDC00001:1.0")).thenReturn(Optional.of(getTestSchema("BSDC00001")));
    when(schemaRepository.findById("BSDC00002:1.0")).thenReturn(Optional.of(getTestSchema("BSDC00002")));
  }

  @Test
  void handleBeforeSave() {
    Field field = getTestField();
    fieldRepositoryEventHandler.handleBeforeSave(field);
    assertThat(field.getVersion()).isEqualTo("1.1");
    assertThat(field.getId()).isEqualTo("handleBeforeSaveField:1.1");
    assertThat(field.getId()).isEqualTo("handleBeforeSaveField:1.1");
    assertThat(field.getUsedBySchemas().stream().findFirst().orElseThrow()).contains(":1.1");
  }
}