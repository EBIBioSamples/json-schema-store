package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@SuperBuilder
@Document(collection = "fields")
@NoArgsConstructor
@TypeAlias("taxon")
public class TaxonField extends Field {
}
