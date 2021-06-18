package uk.ac.ebi.biosamples.jsonschemastore.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class JsonSchemaExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MalformedSchemaException.class})
    public ResponseEntity<Object> handleException(RuntimeException e, WebRequest request) {
        String message = e.getMessage();
        return handleExceptionInternal(e, message, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {ApplicationStateException.class})
    public ResponseEntity<Object> handleApplicationStateException(RuntimeException e, WebRequest request) {
        String message = e.getMessage();
        return handleExceptionInternal(e, message, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
