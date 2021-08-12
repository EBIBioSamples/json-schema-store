package uk.ac.ebi.biosamples.jsonschemastore.exception;

/*
Custom exception for json schema service
 */

import lombok.NonNull;

public class ApplicationStateException extends RuntimeException {
  public ApplicationStateException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApplicationStateException(@NonNull String message) {
    super(message);
  }
}
