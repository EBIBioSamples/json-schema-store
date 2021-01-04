package uk.ac.ebi.biosamples.jsonschemastore.schema.resource;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.schema.service.SchemaSearchService;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/schemas")
public class SchemaSearchController {

  private final SchemaSearchService schemaSearchService;

  public SchemaSearchController(SchemaSearchService schemaSearchService) {
    this.schemaSearchService = schemaSearchService;
  }

  @GetMapping("/page")
  public ResponseEntity<Page<SchemaBlockDocument>> getAllSchemaBlocksPage(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {

    return ResponseEntity.ok(
        schemaSearchService.getAllSchemaBlocksPage(
            Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(size, 3)));
  }

  @GetMapping("/search")
  public ResponseEntity<Page<SchemaBlockDocument>> searchSchemas(
      @RequestParam(value = "key", required = true) @NonNull String searchKey,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size)
      throws JsonSchemaServiceException {
    try {
      return ResponseEntity.ok(
          schemaSearchService.fullTextSearchSchemas(
              searchKey, Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(size, 3)));
    } catch (Exception e) {
      log.error("Error occurred while searching key: " + searchKey, e);
      throw new JsonSchemaServiceException("Error occurred while searching key: " + searchKey, e);
    }
  }

  @GetMapping("/versions")
  public ResponseEntity<Page<SchemaBlockDocument>> querySchemas(
          @RequestParam(value = "schemaName", required = true) @NonNull String schemaName,
          @RequestParam(value = "page", required = false) Integer page,
          @RequestParam(value = "size", required = false) Integer size)
          throws JsonSchemaServiceException {
    try {

      return ResponseEntity.ok(
              schemaSearchService.getListOfVersionsOfSchema(
                      schemaName, Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(size, 3)));
    } catch (Exception e) {
      log.error("Error occurred while searching key: " + schemaName, e);
      throw new JsonSchemaServiceException("Error occurred while searching key: " + schemaName, e);
    }
  }
}
