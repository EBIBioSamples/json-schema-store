package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

@Data
@Document(collection = "fields")
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChoiceField.class, name = "choice"),
        @JsonSubTypes.Type(value = PatternField.class, name = "pattern"),
        @JsonSubTypes.Type(value = Field.class, name = "text")
})
public class Field
{
    @Id
    protected String id; // "name" in the old ena editor
    protected String label;
    protected String description;
    protected Set<String> usedBySchemas = new HashSet<>();;
    protected String type;
    protected String group;
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
