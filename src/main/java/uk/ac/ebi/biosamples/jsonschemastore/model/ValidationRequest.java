package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;


public record ValidationRequest(@NonNull JsonNode schema, @NonNull JsonNode data) {
}
