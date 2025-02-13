package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.extern.java.Log;
import org.springframework.data.mongodb.core.mapping.event.*;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

//@Component
@Log
public class MongoJsonSchemaEventHandler extends AbstractMongoEventListener<MongoJsonSchema> {

    @Override
    public void onBeforeSave(BeforeSaveEvent<MongoJsonSchema> event) {
        log.info("onBeforeSave: " + event.getSource().getId());
        event.getSource().constructTextSearchField();
    }
}
