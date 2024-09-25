package uk.ac.ebi.biosamples.jsonschemastore.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Component
@RepositoryEventHandler(Field.class)
public class FieldRepositoryEventHandler {
    private static final Logger logger = LoggerFactory.getLogger(FieldRepositoryEventHandler.class);
    @HandleBeforeCreate
    public void handleBeforeCreate(Field field) {
        field.setId(toVariableName(field.getLabel()));
    }

}
