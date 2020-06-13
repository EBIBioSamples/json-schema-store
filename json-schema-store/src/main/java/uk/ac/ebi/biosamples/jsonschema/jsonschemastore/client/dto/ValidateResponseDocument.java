package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public class ValidateResponseDocument {
  private final List<String> errors;
  private final String dataPath;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ValidateResponseDocument(
      @JsonProperty("errors") final List<String> errors,
      @JsonProperty("dataPath") final String dataPath) {
    this.errors = errors;
    this.dataPath = dataPath;
  }
}
