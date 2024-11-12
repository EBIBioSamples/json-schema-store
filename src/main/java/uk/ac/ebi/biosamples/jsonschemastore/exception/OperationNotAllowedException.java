package uk.ac.ebi.biosamples.jsonschemastore.exception;

/*
Custom exception for json schema errors
 */

import lombok.NonNull;

public class OperationNotAllowedException extends RuntimeException {
  public OperationNotAllowedException(String message, Throwable cause) {
    super(message, cause);
  }

  public OperationNotAllowedException(@NonNull String message) {
    super(message);
  }
}
