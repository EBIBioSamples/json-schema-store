package uk.ac.ebi.biosamples.jsonschemastore.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleUpdateNotAllowedException(OperationNotAllowedException ex) {
        return new ResponseEntity<>(Map.of("errors",formatErrorResponse(ex, HttpStatus.FORBIDDEN.value())), HttpStatus.FORBIDDEN);
    }

    /**
     * react admin from end requires error response in specific format , so that it can easily interpret and show it in UI
     * @return
     */
    private Map<String, Object> formatErrorResponse(Exception ex, int status) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", status);
        errorBody.put("root", Map.of("serverError", ex.getMessage()));
        return errorBody;
    }
}
