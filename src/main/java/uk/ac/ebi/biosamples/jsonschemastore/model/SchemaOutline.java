package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "schemas")
public class SchemaOutline extends RepresentationModel<SchemaOutline> {
    protected String id;
    protected String name;
    protected String version;
    protected String title;
}
