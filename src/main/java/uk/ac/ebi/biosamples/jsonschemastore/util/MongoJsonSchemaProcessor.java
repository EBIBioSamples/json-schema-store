package uk.ac.ebi.biosamples.jsonschemastore.util;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

/**
 * This RepresentationModelProcessor globally adds custom links to
 * entities processed by Spring Data REST.
 */
@Component
public class MongoJsonSchemaProcessor implements RepresentationModelProcessor<EntityModel<MongoJsonSchema>> {

    @Override
    public EntityModel<MongoJsonSchema> process(EntityModel<MongoJsonSchema> model) {
        return model;
    }
}
