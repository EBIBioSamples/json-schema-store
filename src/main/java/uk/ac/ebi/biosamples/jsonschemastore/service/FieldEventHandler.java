package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

@Component
public class FieldEventHandler extends AbstractMongoEventListener<Field> {

    @Override
    public void onBeforeSave(BeforeSaveEvent<Field> event) {
        event.getSource().constructTextSearchField();
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Field> event) {
        event.getSource().constructTextSearchField();
    }
}
