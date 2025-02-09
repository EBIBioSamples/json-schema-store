package uk.ac.ebi.biosamples.jsonschemastore.service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.exception.OperationNotAllowedException;
import uk.ac.ebi.biosamples.jsonschemastore.model.Authority;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;

import java.time.LocalDateTime;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Component
@RepositoryEventHandler
@RequiredArgsConstructor
public class MongoJsonSchemaRepositoryEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(MongoJsonSchemaRepositoryEventHandler.class);
    public static final String DEFAULT_SCHEMA_VERSION = "1.0";
    private final SchemaService schemaService;
    private final AccessioningService accessioningService;
    private final JsonSchemaExporter jsonSchemaExporter;
    private final UserService userService;
    private final FieldService fieldService;

    /**
     * Called when a new checklist is created. Initialises the version, id, and name
     * @param schema
     */
    @HandleBeforeCreate
    public void handleBeforeCreate(MongoJsonSchema schema) {
        logger.info("Before creating MongoJsonSchema: {}", schema.getId());
        schema.setName(toVariableName(schema.getTitle()));
        schema.setAccession(accessioningService.getSchemaAccession(schema.getId()));
        schema.setAuthority(Authority.ENA.name());
        setVersionAndMarkAsLatest(schema, DEFAULT_SCHEMA_VERSION);
        String username = userService.findCurrentUser().getUsername();
        schema.setCreatedBy(username);
        schema.setLastModifiedBy(username);
        LocalDateTime now = LocalDateTime.now();
        schema.setCreatedDate(now);
        schema.setLastModifiedDate(now);
        populateSearchAndSchemaFields(schema);
    }

    /**
     * called after a checklist is saved (both 1st or n-th time).
     * updates field references
     * @param schema
     */
    @HandleAfterSave
    @HandleAfterCreate
    public void handleAfterCreateOrSave(MongoJsonSchema schema) {
      logger.info("handleAfterCreateOrSave for schema {}", schema.getId());
      fieldService.updateFieldToSchemaRefs(schema);
    }


    /**
     * called before a checklist is updated.
     * makes it uneditable, increments version
     * @param schema
     */
    @HandleBeforeSave
    public void handleBeforeSave(MongoJsonSchema schema) {
        logger.info("Before saving MongoJsonSchema: {}", schema.getId());
        if (!schema.getLatest()) {
            throw new OperationNotAllowedException("Non latest versions are not updatable");
        }
        schemaService.processAndSaveCurrentVersionAsNonLatest(schema.getId());

        // this will generate a new checklist instance with an incremented version
        String incrementedVersion = VersionIncrementer.incrementMinorVersion(schema.getVersion());
        setVersionAndMarkAsLatest(schema, incrementedVersion);
        populateSearchAndSchemaFields(schema);
        String username = userService.findCurrentUser().getUsername();
        schema.setCreatedBy(username);
        schema.setLastModifiedBy(username);
        logger.info("incrementedVersion: {}", incrementedVersion);
    }

    private void setVersionAndMarkAsLatest(MongoJsonSchema schema, String incrementedVersion) {
        schema.setVersion(incrementedVersion);
        schema.setId(new SchemaId(schema.getAccession(), schema.getVersion()).asString());
        schema.makeEditable();
        schema.makeLatest();
    }

    private void populateSearchAndSchemaFields(MongoJsonSchema schema) {
        schema.setSchema(jsonSchemaExporter.generateJsonSchemaFromChecklistFields(schema));
        schema.constructTextSearchField();
    }




}
