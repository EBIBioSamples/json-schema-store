package uk.ac.ebi.biosamples.jsonschemastore.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.client.ValidatorClient;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidateRequestDocument;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidateResponseDocument;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidationState;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaBlockService;
import uk.ac.ebi.biosamples.jsonschemastore.schema.util.JsonSchemaMappingUtil;

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

  @GetMapping("/schemas/")
  public ResponseEntity<SchemaBlockDocument> getAllSchemaBlockById(@RequestParam("id") String id)
      throws JsonSchemaServiceException {
    try {
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
    return ResponseEntity.ok(schemaBlockService.getAllSchemaBlocks());
  }

  @PostMapping("/schemas")
  public ResponseEntity<JsonNode> createSchemaBlock(@RequestBody SchemaBlockDocument schemaBlockDocument) throws JsonSchemaServiceException {
    try {
      ResponseEntity<ValidateResponseDocument> validateResult = this.validateSchema(schemaBlockDocument);
      if (HttpStatus.OK.equals(validateResult.getStatusCode()) && ValidationState.VALID.equals(Objects.requireNonNull(validateResult.getBody()).getValidationState())) {
        SchemaBlockDocument result = schemaBlockService.createSchemaBlock(schemaBlockDocument);
        return new ResponseEntity<>(
            JsonSchemaMappingUtil.convertSchemaBlockToJson(result.getJsonSchema()),
            HttpStatus.CREATED);
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

  @DeleteMapping("/schemas/")
  public ResponseEntity<String> deleteSchemaBlocksById(@RequestParam("id") String id) {
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
        SchemaBlockDocument result = schemaBlockService.updateSchemaBlocks(schemaBlockDocument);
        return new ResponseEntity<>(
            JsonSchemaMappingUtil.convertSchemaBlockToJson(result.getJsonSchema()),
            HttpStatus.CREATED);
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
