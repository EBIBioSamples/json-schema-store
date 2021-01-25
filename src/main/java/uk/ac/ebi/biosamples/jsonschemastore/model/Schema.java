package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public abstract class Schema {
    protected String id;
    protected String name;
    protected String version;
    protected String title;
    protected String description;
    protected String domain;
    protected String metaSchema;
    protected JsonNode schema;
}
