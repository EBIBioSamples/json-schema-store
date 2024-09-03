package uk.ac.ebi.biosamples.jsonschemastore.model.mongo;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;

/**
 * TODO: use {@link org.springframework.data.mongodb.core.mapping.DocumentReference} to connect fields and schema
 */
@Data
@NoArgsConstructor
public class SchemaFieldAssociation {

    String fieldId;
    Property.AttributeCardinality cardinality;
    Multiplicity multiplicity;

}
