package uk.ac.ebi.biosamples.jsonschemastore.ena;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ImportedChecklist {
    private final String jsonSchemaString;
    private final List<Property> properties;

}
