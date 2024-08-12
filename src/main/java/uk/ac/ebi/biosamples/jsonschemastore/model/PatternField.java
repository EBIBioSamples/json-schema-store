package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@SuperBuilder
@Document(collection = "fields")
@NoArgsConstructor
@TypeAlias("pattern")
public class PatternField extends Field {
    String pattern;
}
