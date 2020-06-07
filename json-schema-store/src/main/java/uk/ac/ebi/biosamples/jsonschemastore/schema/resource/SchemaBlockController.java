package uk.ac.ebi.biosamples.jsonschemastore.schema.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.client.ValidatorClient;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidateRequestDocument;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidateResponseDocument;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.schema.service.SchemaBlockService;
import uk.ac.ebi.biosamples.jsonschemastore.schema.util.JsonSchemaMappingUtil;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class SchemaBlockController {

  private final SchemaBlockService schemaBlockService;
  private final ModelMapper modelMapper;
  private final ValidatorClient validatorClient;

  public SchemaBlockController(SchemaBlockService schemaBlockService, ModelMapper modelMapper, ValidatorClient validatorClient) {
    this.schemaBlockService = schemaBlockService;
    this.modelMapper = modelMapper;
    this.validatorClient = validatorClient;
  }

  @GetMapping("/schemas")
  public ResponseEntity<List<JsonNode>> getAllSchemaBlock() {
    List<SchemaBlock> schemaBlocks = schemaBlockService.getAllSchemaBlocks();
    List<JsonNode> response =schemaBlocks.stream()
            .map(JsonSchemaMappingUtil::convertSchemaBlockToJson)
            .collect(Collectors.toList());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/schemas")
  public ResponseEntity<JsonNode> createSchemaBlock(@RequestBody SchemaBlockDocument schema) throws JsonSchemaServiceException {
    try {
      ResponseEntity<ValidateResponseDocument[]> validateResult = this.validateSchema(schema);
      if (HttpStatus.OK.equals(validateResult.getStatusCode())
              && Objects.requireNonNull(validateResult.getBody()).length == 0) {
        SchemaBlock schemaBlock = modelMapper.map(schema, SchemaBlock.class);
        SchemaBlock result = schemaBlockService.createSchemaBlock(schemaBlock);
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

  private ResponseEntity<ValidateResponseDocument[]> validateSchema(SchemaBlockDocument schema) {
    JsonNode jsonNode = JsonSchemaMappingUtil.convertSchemaBlockToJson(schema);
    ValidateRequestDocument validateRequestDocument = new ValidateRequestDocument();
    validateRequestDocument.setObject(jsonNode);
    validateRequestDocument.setSchema(JsonSchemaMappingUtil.getSchemaObject()); // TODO: meta schema should come from document store
    return validatorClient.validate(validateRequestDocument);
  }
}
