package uk.ac.ebi.biosamples.jsonschemastore.exception;

/*
Custom exception for json schema service
 */

import lombok.NonNull;

public class JsonSchemaServiceException extends Exception {
  public JsonSchemaServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public JsonSchemaServiceException(@NonNull String message) {
    super(message);
  }
}
