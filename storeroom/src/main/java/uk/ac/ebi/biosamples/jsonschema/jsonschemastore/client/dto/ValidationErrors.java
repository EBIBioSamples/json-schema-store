package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class ValidationErrors {
  private final Map<String, Object> ajvError;
  private final String message;
  private final String absoluteDataPath;
  private final String userFriendlyMessage;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public ValidationErrors(
      @JsonProperty("ajvError") final Map<String, Object> ajvError,
      @JsonProperty("message") final String message,
      @JsonProperty("absoluteDataPath") final String absoluteDataPath,
      @JsonProperty("userFriendlyMessage") final String userFriendlyMessage) {
    this.ajvError = ajvError;
    this.message = message;
    this.absoluteDataPath = absoluteDataPath;
    this.userFriendlyMessage = userFriendlyMessage;
  }
}
