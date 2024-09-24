package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.server.core.Relation;


import java.util.List;

@Data
@SuperBuilder
@Document(collection = "fields")
@NoArgsConstructor
@TypeAlias("choice")
public class ChoiceField extends Field {
    List<String> choices;
}
