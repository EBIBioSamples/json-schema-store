package uk.ac.ebi.biosamples.jsonschemastore.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateSchemaDTO {
    JsonNode schema;
    JsonNode object;
}
