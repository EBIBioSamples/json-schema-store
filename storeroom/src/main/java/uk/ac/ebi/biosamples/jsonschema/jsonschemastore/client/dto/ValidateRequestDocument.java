package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NonNull
public class ValidateRequestDocument {
  @NonNull JsonNode schema;
  @NonNull JsonNode object;
}
