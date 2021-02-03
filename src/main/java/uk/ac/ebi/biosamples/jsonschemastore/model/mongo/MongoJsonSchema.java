package uk.ac.ebi.biosamples.jsonschemastore.model.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "schemas")
public class MongoJsonSchema {
    @Id
    @TextIndexed(weight = 10F)
    private String id;
    @Indexed(unique=true)
    private String accession;
    @TextIndexed(weight = 10F)
    private String name;
    private String version;
    @TextIndexed(weight = 5F)
    private String title;
    @TextIndexed(weight = 5F)
    private String description;
    private String domain;
    private String metaSchema;
    private String schema;
}
