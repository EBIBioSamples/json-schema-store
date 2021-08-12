package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "schemas")
public class MetaSchema extends Schema {
//    private JsonNode config;
//    private JsonNode template;
}
