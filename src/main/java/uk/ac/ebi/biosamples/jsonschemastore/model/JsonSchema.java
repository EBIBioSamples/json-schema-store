package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "schemas")
public class JsonSchema extends Schema {
}
