package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Document(collection = "fields")
@NoArgsConstructor
@TypeAlias("choice")
public class ChoiceField extends Field {
    List<String> choices;
}
