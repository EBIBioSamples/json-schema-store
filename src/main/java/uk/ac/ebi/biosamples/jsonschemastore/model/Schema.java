package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public abstract class Schema extends RepresentationModel<Schema> {
    protected String id;
    protected String accession;
    protected String name;
    protected String version;
    protected String title;
    protected String description;
    protected String domain;
    protected String metaSchema;
    protected JsonNode schema;
}
