package uk.ac.ebi.biosamples.jsonschemastore.exception;

/*
Custom exception for json schema service
 */

public class JsonSchemaServiceException extends Exception {
  public JsonSchemaServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
