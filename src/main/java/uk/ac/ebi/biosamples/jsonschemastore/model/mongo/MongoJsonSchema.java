package uk.ac.ebi.biosamples.jsonschemastore.model.mongo;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Document(collection = "schemas")
@CompoundIndexes({
        @CompoundIndex(
                name = "accession_version",
                def = "{'accession' : 1, 'version': 1}",
                unique = true)
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
    private List<SchemaFieldAssociation> schemaFieldAssociations = new ArrayList<>();
    @CreatedDate
    private LocalDateTime createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
    @LastModifiedBy
    private String lastModifiedBy;

    private Boolean editable;
    private Boolean latest;
    private String group;
    private String searchable;

    public void setSearchable(String searchable) {
        this.searchable = searchable;
    }

    public void makeNonEditable() {
        this.editable = false;
    }

    public void makeEditable() {
        this.editable = true;
    }

    public void makeNonLatest() {
        this.latest = false;
    }

    public void makeLatest() {
        this.latest = true;
    }

    public void constructTextSearchField() {
        String searchable = Stream.of(this.getAccession(), this.getDescription(), this.getTitle(),this.getName(), this.getGroup())
                .filter(value -> value != null && !value.isEmpty())
                .collect(Collectors.joining(" "));
        this.setSearchable(searchable);

    }
}
