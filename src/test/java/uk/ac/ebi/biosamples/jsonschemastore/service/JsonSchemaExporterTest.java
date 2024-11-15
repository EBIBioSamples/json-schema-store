package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.biosamples.jsonschemastore.FieldGeneratorHelper;
import uk.ac.ebi.biosamples.jsonschemastore.SchemaHelper;
import uk.ac.ebi.biosamples.jsonschemastore.ena.SchemaTemplateGenerator;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.Multiplicity;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class JsonSchemaExporterTest {
  @Mock
  SchemaRepository schemaRepository;
  @Mock
  FieldRepository fieldRepository;
  @Mock
  SchemaTemplateGenerator schemaTemplateGenerator;
  @InjectMocks
  JsonSchemaExporter jsonSchemaExporter;

  @Test
  void listPropertiesShouldConvertUnitsInFieldsCorrectly() {
    String fieldId = "test_field_id";
    Field field = FieldGeneratorHelper.getTestFieldWithIdAndUnits(fieldId, Collections.singletonList("m"));
    Mockito.when(fieldRepository.findById(any())).thenReturn(Optional.of(field));

    SchemaFieldAssociation fieldAssociation = new SchemaFieldAssociation(fieldId, Property.RequirementType.MANDATORY, Multiplicity.Single);
    MongoJsonSchema schema = SchemaHelper.getMongoJsonSchemaWithFieldAssociations(List.of(fieldAssociation));
    List<Property> properties = jsonSchemaExporter.listProperties(schema);
    Assertions.assertThat(properties.get(0).units().contains("m")).isTrue();
  }
  
}