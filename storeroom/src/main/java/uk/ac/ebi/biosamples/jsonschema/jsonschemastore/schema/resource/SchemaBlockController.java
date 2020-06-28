package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.ValidatorClient;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto.ValidateRequestDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto.ValidateResponseDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto.ValidationState;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service.SchemaBlockService;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.util.JsonSchemaMappingUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SchemaBlockController {

  private final SchemaBlockService schemaBlockService;
  private final ValidatorClient validatorClient;
  private final Environment environment;

  public SchemaBlockController(
      SchemaBlockService schemaBlockService,
      ValidatorClient validatorClient,
      Environment environment) {
    this.schemaBlockService = schemaBlockService;
    this.validatorClient = validatorClient;
    this.environment = environment;
  }

  @GetMapping("/schemas/**")
  public ResponseEntity<SchemaBlockDocument> getAllSchemaBlockById(HttpServletRequest request)
      throws JsonSchemaServiceException {
    try {
      String fullUrl = request.getRequestURL().toString();
      String[] s = fullUrl.split("/api/v1/schemas/");
      String id = String.join("", Arrays.copyOfRange(s, 1, s.length));
      return schemaBlockService
          .getAllSchemaBlocksById(id)
          .map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.notFound().build());
    } catch (Exception e) {
      String errorMessage = "Error occurred while getting schema: ";
      log.error(errorMessage, e);
      throw new JsonSchemaServiceException(errorMessage, e);
    }
  }

  @GetMapping("/schemas")
  public ResponseEntity<List<SchemaBlockDocument>> getAllSchemaBlocks() {
    schemaBlockService.getAllSchemaBlocks();
    return ResponseEntity.ok(schemaBlockService.getAllSchemaBlocks());
  }

  @PostMapping("/schemas")
  public ResponseEntity<JsonNode> createSchemaBlock(@RequestBody SchemaBlockDocument schemaBlockDocument) throws JsonSchemaServiceException {
    try {
      ResponseEntity<ValidateResponseDocument> validateResult = this.validateSchema(schemaBlockDocument);
      if (HttpStatus.OK.equals(validateResult.getStatusCode()) && ValidationState.VALID.equals(Objects.requireNonNull(validateResult.getBody()).getValidationState())) {
        SchemaBlock result = schemaBlockService.createSchemaBlock(schemaBlockDocument);
        return new ResponseEntity<>(JsonSchemaMappingUtil.convertSchemaBlockToJson(result), HttpStatus.CREATED);
      } else {
        return ResponseEntity.badRequest().body(JsonSchemaMappingUtil.convertObjectToJson(validateResult.getBody()));
      }
    } catch (Exception e) {
      String errorMessage = "Error occurred while creating schema ";
      log.error(errorMessage, e);
      throw new JsonSchemaServiceException(errorMessage, e);
    }
  }

  @DeleteMapping("/schemas")
  public ResponseEntity<String> deleteSchemaBlocks(
      @RequestBody SchemaBlockDocument schemaBlockDocument) {
    schemaBlockService.deleteSchemaBlocks(schemaBlockDocument);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/schemas/{id}")
  public ResponseEntity<String> deleteSchemaBlocksById(@PathVariable("id") String id) {
    schemaBlockService.deleteSchemaBlocksById(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/schemas")
  public ResponseEntity<JsonNode> updateSchemaBlocks(
      @RequestBody SchemaBlockDocument schemaBlockDocument) throws JsonSchemaServiceException {
    try {
      ResponseEntity<ValidateResponseDocument> validateResult =
          this.validateSchema(schemaBlockDocument);
      if (HttpStatus.OK.equals(validateResult.getStatusCode())
          && ValidationState.VALID.equals(
              Objects.requireNonNull(validateResult.getBody()).getValidationState())) {
        SchemaBlock result = schemaBlockService.updateSchemaBlocks(schemaBlockDocument);
        return new ResponseEntity<>(
            JsonSchemaMappingUtil.convertSchemaBlockToJson(result), HttpStatus.CREATED);
      } else {
        return ResponseEntity.badRequest()
            .body(JsonSchemaMappingUtil.convertObjectToJson(validateResult.getBody()));
      }
    } catch (Exception e) {
      String errorMessage = "Error occurred while updating schema ";
      log.error(errorMessage, e);
      throw new JsonSchemaServiceException(errorMessage, e);
    }
  }

  private ResponseEntity<ValidateResponseDocument> validateSchema(SchemaBlockDocument schema)
      throws JsonProcessingException {
    JsonNode jsonNode = JsonSchemaMappingUtil.convertSchemaBlockToJson(schema.getJsonSchema());
    ValidateRequestDocument validateRequestDocument = new ValidateRequestDocument();
    validateRequestDocument.setObject(jsonNode);
    validateRequestDocument.setSchema(JsonSchemaMappingUtil.getSchemaObject()); // TODO: meta schema should come from document store
    return validatorClient.validate(validateRequestDocument, environment.getProperty("elixirValidator.hostUrl"));
  }
}
