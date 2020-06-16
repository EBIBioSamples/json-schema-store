package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class ValidateResponseDocument {
  private final ValidationState validationState;
  private final String validationJobId;
  private final List<ValidationErrors> validationErrors;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ValidateResponseDocument(
      @JsonProperty("validationState") final ValidationState validationState,
      @JsonProperty("validationJobId") final String validationJobId,
      @JsonProperty("validationErrors") final List<ValidationErrors> validationErrors) {
    this.validationState = validationState;
    this.validationJobId = validationJobId;
    this.validationErrors = validationErrors;
  }
}
