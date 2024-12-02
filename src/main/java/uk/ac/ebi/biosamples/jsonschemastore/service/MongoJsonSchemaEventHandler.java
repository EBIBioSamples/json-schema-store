package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

@Component
public class MongoJsonSchemaEventHandler extends AbstractMongoEventListener<MongoJsonSchema> {

    @Override
    public void onBeforeSave(BeforeSaveEvent<MongoJsonSchema> event) {
        event.getSource().constructTextSearchField();
    }
}
