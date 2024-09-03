package uk.ac.ebi.biosamples.jsonschemastore.model.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "schemas")
@CompoundIndexes({
        @CompoundIndex(name = "accession_version", def = "{'accession' : 1, 'version': 1}")
})
public class MongoJsonSchema {
    @Id
    @TextIndexed(weight = 10F)
    private String id;
    @TextIndexed(weight = 10F)
    private String accession;
    @TextIndexed(weight = 5F)
    private String title;
    @TextIndexed(weight = 5F)
    private String description;
    private String metaSchema;
    private String schema;
    @TextIndexed(weight = 10F)
    private String name;
    @TextIndexed(weight = 10F)
    private String version;
    private String domain;
    private String authority;
    private List<SchemaFieldAssociation> schemaFieldAssociations;
}
