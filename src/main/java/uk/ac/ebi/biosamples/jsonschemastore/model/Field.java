package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

@Data
@Document(collection = "fields")
@SuperBuilder
@NoArgsConstructor
public class Field
{
    @Id
    protected String id; // "name" in the old ena editor
    protected String label;
    protected String description;
    protected List<String> usedBySchemas;
}

