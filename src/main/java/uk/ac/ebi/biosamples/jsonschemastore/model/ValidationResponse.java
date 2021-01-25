package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidationErrors;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidationState;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class ValidationResponse {
  private final ValidationState validationState;
  private final String validationJobId;
  private final List<ValidationErrors> validationErrors;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ValidationResponse(
      @JsonProperty("validationState") final ValidationState validationState,
      @JsonProperty("validationJobId") final String validationJobId,
      @JsonProperty("validationErrors") final List<ValidationErrors> validationErrors) {
    this.validationState = validationState;
    this.validationJobId = validationJobId;
    this.validationErrors = validationErrors;
  }
}
