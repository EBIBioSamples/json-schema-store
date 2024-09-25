package uk.ac.ebi.biosamples.jsonschemastore.util;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
        MongoJsonSchema schema = model.getContent();

        // Add a custom self link
        if (schema != null) {
//            Link selfLink = WebMvcLinkBuilder.linkTo(
//                    WebMvcLinkBuilder.methodOn(SchemaSearchController.class)
//                            .findByExample(schema)
//            ).withSelfRel();
//
//            model.add(selfLink);

            // Add any other custom links
//            Link customLink = WebMvcLinkBuilder.linkTo(
//                    WebMvcLinkBuilder.methodOn(SchemaSearchController.class)
//                            .someOtherMethod(schema.getId())  // Hypothetical method
//            ).withRel("related-resource");

//            model.add(customLink);
        }

        return model;
    }
}
