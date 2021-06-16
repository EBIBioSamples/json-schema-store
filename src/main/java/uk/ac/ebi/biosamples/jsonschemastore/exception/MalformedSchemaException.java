package uk.ac.ebi.biosamples.jsonschemastore.exception;

/*
Custom exception for json schema errors
 */

import lombok.NonNull;

public class MalformedSchemaException extends RuntimeException {
  public MalformedSchemaException(String message, Throwable cause) {
    super(message, cause);
  }

  public MalformedSchemaException(@NonNull String message) {
    super(message);
  }
}
