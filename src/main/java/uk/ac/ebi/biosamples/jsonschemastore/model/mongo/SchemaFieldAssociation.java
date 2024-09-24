package uk.ac.ebi.biosamples.jsonschemastore.model.mongo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;

/**
 * TODO: use {@link org.springframework.data.mongodb.core.mapping.DocumentReference} to connect fields and schema
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class SchemaFieldAssociation {

    @NonNull
    String fieldId;
    @NonNull Property.AttributeCardinality cardinality;
    @NonNull Multiplicity multiplicity;

}
