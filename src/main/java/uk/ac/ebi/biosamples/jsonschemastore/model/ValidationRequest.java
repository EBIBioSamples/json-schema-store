package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NonNull;

@Data
public class ValidationRequest {
    @NonNull JsonNode schema;
    @NonNull JsonNode object;
}
