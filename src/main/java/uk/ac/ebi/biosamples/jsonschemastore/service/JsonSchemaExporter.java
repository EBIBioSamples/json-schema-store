package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.ena.SchemaTemplateGenerator;
import uk.ac.ebi.biosamples.jsonschemastore.exception.ApplicationStateException;
import uk.ac.ebi.biosamples.jsonschemastore.model.*;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.Multiplicity;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonSchemaExporter {
  private final SchemaTemplateGenerator schemaTemplateGenerator;
  private final SchemaRepository schemaRepository;
  private final FieldRepository fieldRepository;

  private static Property.Multiplicity convertToPropertyMuliplicity(Multiplicity multiplicity) {
    return multiplicity == Multiplicity.Single ? Property.Multiplicity.SINGLE : Property.Multiplicity.MULTIPLE;
  }

  private static String getTypedTemplate(Field field) {
    String fieldTypeTemplate;
    if (field instanceof ChoiceField cf) {
      fieldTypeTemplate = SchemaTemplateGenerator.getEnumTemplate(cf.getChoices());
    } else if (field instanceof PatternField pf) {
      fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("pattern", pf.getPattern(), 0, 0, "");
    } else if (field instanceof TaxonField tf) {
      fieldTypeTemplate = SchemaTemplateGenerator.getTaxonTemplate();
    } else if (field instanceof OntologyField of) {
      fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("ontology", of.getOntology(), 0, 0, "");
    } else {
      fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("", "", 0, 0, "");
    }

    return fieldTypeTemplate;
  }

  public String generateJsonSchemaFromChecklistFields(String checklistId) {
    MongoJsonSchema schema = schemaRepository.findById(checklistId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid checklistId: " + checklistId));
    return generateJsonSchemaFromChecklistFields(schema);
  }

  public String generateJsonSchemaFromChecklistFields(MongoJsonSchema schema) {
    try {
      List<Property> properties = listProperties(schema);
      String schemaId = new SchemaId(schema.getAccession(), schema.getVersion()).asString();
      String title = schema.getTitle();
      String description = schema.getDescription();
      return schemaTemplateGenerator.getBioSamplesSchema(schemaId, title, description, properties, true);
    } catch (Exception e) {
      log.error("Could not convert checklist: {}", schema.getId(), e);
      throw new ApplicationStateException("Could not convert checklist for " + schema.getId(), e);
    }
  }

  protected List<Property> listProperties(MongoJsonSchema schema) {
    List<Property> properties = new ArrayList<>();
    for (SchemaFieldAssociation fieldAssociation : schema.getSchemaFieldAssociations()) {
      Field field = fieldRepository.findById(fieldAssociation.getFieldId())
          .orElseThrow(() -> new ApplicationStateException("Invalid schema state. Field can not be found: " + fieldAssociation.getFieldId()));
      Property property = new Property(field.getLabel(),
              Collections.emptyList(),
              field.getDescription(),
              getTypedTemplate(field),
              Optional.ofNullable(field.getUnits()).map(ArrayList::new).orElse(new ArrayList<>()),
              fieldAssociation.getRequirementType(),
              convertToPropertyMuliplicity(fieldAssociation.getMultiplicity()),
              "");
      properties.add(property);
    }
    return properties;
  }
}
