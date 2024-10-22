package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.hateoas.RepresentationModel;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    protected String authority;
    protected List<SchemaFieldAssociation> schemaFieldAssociations = new ArrayList<>();
    private Boolean editable;
    private Boolean latest;
    private String group;
}
