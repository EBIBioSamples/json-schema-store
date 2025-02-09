package uk.ac.ebi.biosamples.jsonschemastore.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import uk.ac.ebi.biosamples.jsonschemastore.exception.OperationNotAllowedException;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.List;
import java.util.Set;

@Component
@RepositoryEventHandler(Field.class)
@Slf4j
@RequiredArgsConstructor
public class FieldRepositoryEventHandler {
    private final SchemaRepository schemaRepository;
    private final JsonSchemaExporter jsonSchemaExporter;
    private final FieldService fieldService;
    private final FieldGroupService fieldGroupService;
    private final SchemaService schemaService;


    @HandleBeforeCreate
    public void handleBeforeCreate(Field field) {
        if(field.getGroup() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "group cannot be null");
        };
        fieldService.initNewField(field);
    }

    @HandleBeforeSave
    public void handleBeforeSave(Field field) {
        String oldFieldId = field.getId();
        Field oldField = fieldService.safeGetFieldById(field);
        if (!fieldService.isUpdateAllowed(oldField)) {
            throw new OperationNotAllowedException("Non latest versions are not updatable");
        }
        // if only group changed - save and don't increment version
        if (fieldService.isGroupOnlyModification(oldField, field)) {
            // in case only the group changed, no version increment is required
            fieldGroupService.updateGroups(field.getLabel(), oldField.getGroup(), field.getGroup());
            fieldService.save(field);
        } else { // additional fields but the group are modified
            oldField.markNotLatest();
            fieldService.save(oldField);

            if (fieldService.isLabelChange(field, oldField)) {
                // in case of a label change, a new field is created
                fieldService.initNewField(field);
                log.info("Changing field label: from {} to {}", oldField.getLabel(), field.getLabel());
            } else {
                // in case the label stays the same, a new version of the field is created
                String incrementedVersion = VersionIncrementer.incrementMinorVersion(field.getVersion());
                fieldService.initNewField(field, incrementedVersion);
                log.info("Updating field: {} and incrementing version to: {}", oldFieldId, incrementedVersion);

                fieldGroupService.updateGroups(field.getLabel(), oldField.getGroup(), field.getGroup());
            }
            fieldService.updateUsedBySchemas(field, oldFieldId);
        }
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
}
