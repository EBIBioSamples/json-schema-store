package uk.ac.ebi.biosamples.jsonschemastore.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldId;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Component
@RepositoryEventHandler
@Slf4j
public class FieldRepositoryEventHandler {
  private final SchemaRepository schemaRepository;
  private final FieldRepository fieldRepository;

  public FieldRepositoryEventHandler(SchemaRepository schemaRepository, FieldRepository fieldRepository) {
    this.schemaRepository = schemaRepository;
    this.fieldRepository = fieldRepository;
  }

  @HandleBeforeCreate
  public void handleBeforeCreate(Field field) {
    field.setVersion("1.0");
    field.setName(toVariableName(field.getLabel()));
    field.setId(new FieldId(field.getName(), field.getVersion()).asString());
  }

  @HandleBeforeSave
  public void handleBeforeSave(Field field) {
    String oldFieldId = field.getId();
    Field oldField = fieldRepository.findById(field.getId())
        .orElseThrow(() -> new DataRetrievalFailureException("Could not find the field: " + oldFieldId));
    if (!oldField.getLabel().equals(field.getLabel())) {
      throw new DataIntegrityViolationException("Field `label` could not be edited once created. Please create a new field instead.");
    }

    String incrementedVersion = VersionIncrementer.incrementMinorVersion(field.getVersion());
    field.setVersion(incrementedVersion);
    field.setId(new FieldId(field.getName(), field.getVersion()).asString());
    log.info("Updating field: {} and incrementing version to: {}", oldFieldId, incrementedVersion);

    Set<String> schemaIds = field.getUsedBySchemas();
    Set<String> updatedSchemaIds = updateUsedBySchemas(schemaIds, field, oldFieldId);
    field.setUsedBySchemas(updatedSchemaIds);
  }

  private Set<String> updateUsedBySchemas(Set<String> schemas, Field field, String oldFieldId) {
    Set<String> updatedSchemaIds = new HashSet<>();
    for (String schemaId : schemas) {
      String updatedSchemaId = updateSchemaAndIncrementVersion(field, oldFieldId, schemaId);
      updatedSchemaIds.add(updatedSchemaId);
    }
    return updatedSchemaIds;
  }

  private String updateSchemaAndIncrementVersion(Field field, String oldFieldId, String schemaId) {
    log.info("Updating field: {} in schema: {}", field.getId(), schemaId);
    MongoJsonSchema schema = schemaRepository.findById(schemaId)
        .orElseThrow(() -> new DataRetrievalFailureException("Could not find expected schema: " + schemaId));
    SchemaFieldAssociation fieldAssociation = schema.getSchemaFieldAssociations().stream()
        .filter(f -> f.getFieldId().equals(oldFieldId))
        .findFirst()
        .orElseThrow(() -> new DataIntegrityViolationException(
            "Expected field: " + oldFieldId + " could not be found in schema: " + schemaId));
    fieldAssociation.setFieldId(field.getId());
    schema.setVersion(VersionIncrementer.incrementMinorVersion(schema.getVersion()));
    schema.setId(new SchemaId(schema.getAccession(), schema.getVersion()).asString());
    log.info("Updating schema: {} and incrementing version to: {}", schemaId, schema.getVersion());
    schemaRepository.save(schema);
    return schema.getId();
  }
}
