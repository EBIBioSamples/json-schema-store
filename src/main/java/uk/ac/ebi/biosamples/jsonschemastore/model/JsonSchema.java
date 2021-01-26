package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "schemas")
public class JsonSchema extends Schema {
//    private String id;
//    private String name;
//    private String version;
//    private String title;
//    private String description;
//    private String domain;
//    private String metaSchema;
//    private JsonNode schema;
}
