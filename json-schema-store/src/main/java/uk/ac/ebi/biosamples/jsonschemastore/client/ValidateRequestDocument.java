package uk.ac.ebi.biosamples.jsonschemastore.client;

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
  JsonNode schema;
  JsonNode object;
}
