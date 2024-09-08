package uk.ac.ebi.biosamples.jsonschemastore.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.*;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Component
@RepositoryEventHandler(MongoJsonSchema.class)
public class MongoJsonSchemaRepostioryEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(MongoJsonSchemaRepostioryEventHandler.class);

    private final FieldRepository fieldRepository;
    private final SchemaRepository schemaRepository;

    public MongoJsonSchemaRepostioryEventHandler(FieldRepository fieldRepository, SchemaRepository schemaRepository) {
        this.fieldRepository = fieldRepository;
        this.schemaRepository = schemaRepository;
    }

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
                .findFirst()
                .map(Optional::get)
                .ifPresent(field-> {
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

    @HandleBeforeCreate
    public void handleBeforeCreate(MongoJsonSchema schema) {
        logger.info("Before creating MongoJsonSchema: {}", schema.getId());
        schema.setName(toVariableName(schema.getTitle()));
        schema.setVersion("1.0.0");
        schema.setId(schema.getName()+":"+schema.getVersion());
        schema.setAccession(getNextAccession());
    }

    private String getNextAccession() {
        // TODO: won't work correctly when deleted
        return "ERC" + 900000 + schemaRepository.count();
    }


    @HandleBeforeSave
    public void handleBeforeSave(MongoJsonSchema schema) {
        logger.info("Before saving MongoJsonSchema: {}", schema.getId());
        String incrementedVersion = getIncrementedVersion(schema);
        logger.info("incrementedVersion: {}", incrementedVersion);
        schema.setVersion(incrementedVersion);
        schema.setId(schema.getName()+":"+schema.getVersion());
    }

    private static String getIncrementedVersion(MongoJsonSchema schema) {
        String incrementedVersion = incrementMinorVersion(schema.getVersion());
        return incrementedVersion;
    }

    private static String incrementMinorVersion(String version) {
        String[] versionComponents = version.split("\\.");
        String buildNumber = versionComponents[2];
        versionComponents[2] = String.valueOf(Integer.valueOf(buildNumber)+1);
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
