package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto.ValidateResponseDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service.ValidateSchemaService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ValidateSchemaController {

  private final ValidateSchemaService validateSchemaService;

  public ValidateSchemaController(ValidateSchemaService validateSchemaService) {
    this.validateSchemaService = validateSchemaService;
  }

  @PostMapping("/validate")
  public ResponseEntity<ValidateResponseDocument> validateSchema(
      @RequestBody SchemaBlockDocument schemaBlockDocument) throws JsonSchemaServiceException {
    try {
      return validateSchemaService.validateSchema(schemaBlockDocument);
    } catch (JsonProcessingException e) {
      log.error("Error occurred while validating the schema:", e);
      throw new JsonSchemaServiceException("Error occurred while validating the schema : ", e);
    }
  }
}
