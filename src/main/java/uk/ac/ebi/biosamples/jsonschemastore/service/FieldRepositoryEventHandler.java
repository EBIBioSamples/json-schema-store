package uk.ac.ebi.biosamples.jsonschemastore.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.exception.OperationNotAllowedException;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldGroup;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldId;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldGroupRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.HashSet;
import java.util.Set;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Component
@RepositoryEventHandler(Field.class)
@Slf4j
@RequiredArgsConstructor
public class FieldRepositoryEventHandler {
  private final SchemaRepository schemaRepository;
  private final FieldRepository fieldRepository;
  private final JsonSchemaExporter jsonSchemaExporter;
  private final FieldGroupRepository fieldGroupRepository;

  @HandleBeforeCreate
  public void handleBeforeCreate(Field field) {
    field.setVersion("1.0");
    field.setName(toVariableName(field.getLabel()));
    field.setId(new FieldId(field.getName(), field.getVersion()).asString());
    field.setLatest(true);
  }

  @HandleBeforeSave
  public void handleBeforeSave(Field field) {
    String oldFieldId = field.getId();
    Field oldField = fieldRepository.findById(field.getId())
        .orElseThrow(() -> new DataIntegrityViolationException("Could not find the field: " + oldFieldId));
    if(!oldField.isLatest()) {
      throw new OperationNotAllowedException("Non latest versions are not updatable");
    }
    if (!oldField.getLabel().equals(field.getLabel())) {
      throw new DataIntegrityViolationException("Attribute `label` could not be edited once created. Please create a new field instead.");
    }

    if (minorVersionIncrementFieldChanged(oldField, field)) {
      oldField.setLatest(false);
      fieldRepository.save(oldField);

      String incrementedVersion = VersionIncrementer.incrementMinorVersion(field.getVersion());
      field.setVersion(incrementedVersion);
      field.setId(new FieldId(field.getName(), field.getVersion()).asString());
      field.setLatest(true);
      log.info("Updating field: {} and incrementing version to: {}", oldFieldId, incrementedVersion);

      Set<String> schemaIds = field.getUsedBySchemas();
      Set<String> updatedSchemaIds = updateUsedBySchemas(schemaIds, field, oldFieldId);
      field.setUsedBySchemas(updatedSchemaIds);
    }
    updateGroups(field.getLabel(), oldField.getGroup(), field.getGroup());
  }

  @HandleAfterSave
  public void handleAfterSave(Field field) {
    Set<String> schemaIds = field.getUsedBySchemas();
    for (String schemaId : schemaIds) {
      MongoJsonSchema schema = schemaRepository.findById(schemaId)
          .orElseThrow(() -> new DataIntegrityViolationException("Invalid schema reference: " + schemaId));
      schema.setSchema(jsonSchemaExporter.generateJsonSchemaFromChecklistFields(schema));
      schemaRepository.save(schema);
    }
  }

  private Set<String> updateUsedBySchemas(Set<String> schemas, Field field, String oldFieldId) {
    Set<String> updatedSchemaIds = new HashSet<>();
    for (String schemaId : schemas) {
      String updatedSchemaId = updateSchemaFieldAssociationAndIncrementVersion(field, oldFieldId, schemaId);
      updatedSchemaIds.add(updatedSchemaId);
    }
    return updatedSchemaIds;
  }

  private String updateSchemaFieldAssociationAndIncrementVersion(Field field, String oldFieldId, String schemaId) {
    log.info("Updating field: {} in schema: {}", field.getId(), schemaId);
    MongoJsonSchema schema = schemaRepository.findById(schemaId)
        .orElseThrow(() -> new DataIntegrityViolationException("Invalid schema reference: " + schemaId));
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

  protected void updateGroups(String fieldLabel, String oldGroupId, String newGroupId) {
    if (oldGroupId.equals(newGroupId)) {
      return;
    }

    removeFieldFromGroup(fieldLabel, oldGroupId);
    addFieldToGroup(fieldLabel, newGroupId);
  }

  private FieldGroup removeFieldFromGroup(String fieldName, String oldGroupId) {
    FieldGroup oldGroup = fieldGroupRepository.findById(oldGroupId)
        .orElseThrow(() -> new DataIntegrityViolationException("Invalid group id: " + oldGroupId));
    oldGroup.getFields().remove(fieldName);
    fieldGroupRepository.save(oldGroup);
    return oldGroup;
  }

  private void addFieldToGroup(String fieldName, String newGroupId) {
    FieldGroup newGroup = fieldGroupRepository.findById(newGroupId)
        .orElseThrow(() -> new DataIntegrityViolationException("Invalid group id: " + newGroupId));
    newGroup.getFields().add(fieldName);
    fieldGroupRepository.save(newGroup);
  }

  protected boolean minorVersionIncrementFieldChanged(Field oldField, Field newField) {
    return !oldField.getName().equals(newField.getName()) ||
        !oldField.getVersion().equals(newField.getVersion()) ||
        !oldField.getLabel().equals(newField.getLabel()) ||
        !oldField.getDescription().equals(newField.getDescription()) ||
        !oldField.getUsedBySchemas().equals(newField.getUsedBySchemas()) ||
        !oldField.getType().equals(newField.getType()) ||
        !oldField.getUnits().equals(newField.getUnits());
  }
}
