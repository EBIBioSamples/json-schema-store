package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;

import java.util.List;

@Data
public class Checklist {
    private String schemaId;
    private String accession;
    private String domain;
    private String name;
    private String version;
    private String title;
    private String description;
    private String metaSchema;
    private List<ChecklistField> fields;
}
