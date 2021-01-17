package uk.ac.ebi.biosamples.jsonschemastore.model.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "schemas")
public class MongoJsonSchema {
    @Id
    private String id;
    private String name;
    private String version;
    private String title;
    private String description;
    private String domain;
    private String metaSchema;
    private String schema;
}
