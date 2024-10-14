package uk.ac.ebi.biosamples.jsonschemastore.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldId;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Component
@RepositoryEventHandler(Field.class)
public class FieldRepositoryEventHandler {
<<<<<<< Updated upstream
    private static final Logger logger = LoggerFactory.getLogger(FieldRepositoryEventHandler.class);
    @HandleBeforeCreate
    public void handleBeforeCreate(Field field) {
        field.setVersion("1.0");
        field.setName(toVariableName(field.getLabel()));
        field.setId(new FieldId(field.getName(), field.getVersion()).asString());
=======
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
    field.setLatest(true);
  }

  @HandleBeforeSave
  public void handleBeforeSave(Field field) {
    String oldFieldId = field.getId();
    Field oldField = fieldRepository.findById(field.getId())
        .orElseThrow(() -> new DataIntegrityViolationException("Could not find the field: " + oldFieldId));
    if (!oldField.getLabel().equals(field.getLabel())) {
      throw new DataIntegrityViolationException("Attribute `label` could not be edited once created. Please create a new field instead.");
>>>>>>> Stashed changes
    }
    oldField.setLatest(false);
    fieldRepository.save(oldField);

<<<<<<< Updated upstream
=======
    String incrementedVersion = VersionIncrementer.incrementMinorVersion(field.getVersion());
    field.setVersion(incrementedVersion);
    field.setId(new FieldId(field.getName(), field.getVersion()).asString());
    field.setLatest(true);
    log.info("Updating field: {} and incrementing version to: {}", oldFieldId, incrementedVersion);
>>>>>>> Stashed changes

    @HandleBeforeSave
    public void handleBeforeSave(Field field) {
        String incrementedVersion = VersionIncrementer.incrementMinorVersion(field.getVersion());
        logger.info("incrementedVersion: {}", incrementedVersion);
        field.setVersion(incrementedVersion);
        field.setId(new FieldId(field.getName(), field.getVersion()).asString());

        // TODO
        // 1. find all checklists that use this field version
        // 2. create a new version for them that contains the new field version
    }
}
