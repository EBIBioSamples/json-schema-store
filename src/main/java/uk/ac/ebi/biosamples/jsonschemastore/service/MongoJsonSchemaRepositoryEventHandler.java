package uk.ac.ebi.biosamples.jsonschemastore.service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.Authority;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Component
@RepositoryEventHandler
@RequiredArgsConstructor
public class MongoJsonSchemaRepositoryEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(MongoJsonSchemaRepositoryEventHandler.class);
    private final FieldRepository fieldRepository;
    private final SchemaRepository schemaRepository;
    private final AccessioningService accessioningService;

    /**
     * Called when a new checklist is created. Initialises the version, id, and name
     * @param schema
     */
    @HandleBeforeCreate
    public void handleBeforeCreate(MongoJsonSchema schema) {
        logger.info("Before creating MongoJsonSchema: {}", schema.getId());
        schema.setName(toVariableName(schema.getTitle()));
        schema.setVersion("1.0");
        schema.setId(schema.getName()+":"+schema.getVersion());
        schema.setAccession(accessioningService.getSchemaAccession(schema.getId()));
        schema.setAuthority(Authority.BIOSAMPLES.name());
        schema.makeEditable();
        schema.makeLatest();
    }

    /**
     * called after a checklist is saved (both 1st or n-th time).
     * updates field references
     * @param schema
     */
    @HandleAfterSave
    @HandleAfterCreate
    public void handleAfterCreateOrSave(MongoJsonSchema schema) {
        logger.info("handleAfterCreateOrSave for schema " + schema.getId());
        updateFieldToSchemaRefs(schema);
    }

    private void updateFieldToSchemaRefs(MongoJsonSchema schema) {
        // 1. add the schema to all its fields' lists
        Set<String> schemaFieldIds = schema.getSchemaFieldAssociations()
                .stream()
                .map(SchemaFieldAssociation::getFieldId)
                .collect(Collectors.toSet());
        schemaFieldIds.stream()
                .map(fieldRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(field-> {
                    field.getUsedBySchemas().add(schema.getId());
                    fieldRepository.save(field);
                });

        // 2. remove this schema from fields not in its list
        fieldRepository.findByUsedBySchemas(schema.getId())
                .stream()
                .filter(field -> !schemaFieldIds.contains(field.getId()))
                .forEach(field -> {
                    field.getUsedBySchemas().remove(schema.getId());
                    fieldRepository.save(field);
                });
    }


    /**
     * called before a checklist is updated.
     * makes it uneditable, increments version
     * @param newSchemaVersion
     */
    @HandleBeforeSave
    public void handleBeforeSave(MongoJsonSchema newSchemaVersion) {
        logger.info("Before saving MongoJsonSchema: {}", newSchemaVersion.getId());
        // make the current version not editable
        MongoJsonSchema currentSchemaVersion = schemaRepository.findById(newSchemaVersion.getId()).get();
        currentSchemaVersion.makeNonEditable();
        currentSchemaVersion.makeNonLatest();
        schemaRepository.save(currentSchemaVersion);

        // this will generate a new checklist instance with an incremented version
        String incrementedVersion = getIncrementedVersion(newSchemaVersion);
        logger.info("incrementedVersion: {}", incrementedVersion);
        newSchemaVersion.setVersion(incrementedVersion);
        newSchemaVersion.setId(newSchemaVersion.getName()+":"+newSchemaVersion.getVersion());
        newSchemaVersion.makeEditable();
        newSchemaVersion.makeLatest();
    }

    private static String getIncrementedVersion(MongoJsonSchema schema) {
        return incrementMinorVersion(schema.getVersion());
    }

    private static String incrementMinorVersion(String version) {
        String[] versionComponents = version.split("\\.");
        String buildNumber = versionComponents[1];
        versionComponents[1] = String.valueOf(Integer.valueOf(buildNumber)+1);
        return String.join(".", versionComponents);
    }

    @HandleBeforeDelete
    public void handleBeforeDelete(MongoJsonSchema schema) {
        logger.info("Before deleting MongoJsonSchema: {}", schema.getId());
    }

    @HandleAfterDelete
    public void handleAfterDelete(MongoJsonSchema schema) {
        logger.info("After deleting MongoJsonSchema: {}", schema.getId());
    }
}
