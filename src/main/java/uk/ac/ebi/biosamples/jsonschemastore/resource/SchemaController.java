package uk.ac.ebi.biosamples.jsonschemastore.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.ValidationResponse;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaValidationService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaIdExtractor;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v2/schemas")
public class SchemaController {
    private final SchemaService schemaService;
    private final SchemaValidationService schemaValidationService;
    private final SchemaIdExtractor schemaIdExtractor;

    public SchemaController(SchemaService schemaService, SchemaValidationService schemaValidationService,
                            SchemaIdExtractor schemaIdExtractor) {
        this.schemaService = schemaService;
        this.schemaValidationService = schemaValidationService;
        this.schemaIdExtractor = schemaIdExtractor;
    }

    @GetMapping("/id")
    public ResponseEntity<JsonSchema> getSchemaById(@RequestParam("id") String id) {
        return schemaService.getSchemaById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{version}/{schemaName}")
    public ResponseEntity<JsonSchema> getSchemaWithVersion(@PathVariable("schemaName") String schemaName,
                                                           @PathVariable("version") String version) {
        return schemaService.getSchemaByNameAndVersion(schemaName, version)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<JsonSchema>> getSchemaPage(@RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "size", required = false) Integer size) {

        int effectivePage = Objects.requireNonNullElse(page, 0);
        int effectiveSize = Objects.requireNonNullElse(size, 10);
        return ResponseEntity.ok(schemaService.getSchemaPage(effectivePage, effectiveSize));
    }

    @PostMapping
    public ResponseEntity<JsonSchema> createSchema(@RequestBody JsonSchema schema) {
        populateSchemaRequestFields(schema);
        if (schemaService.schemaExists(schema.getId())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            schemaValidationService.validate(schema);
        } catch (JsonSchemaServiceException e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(schemaService.createSchema(schema), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<JsonSchema> updateSchema(@RequestParam("id") String id, @RequestBody JsonSchema schema) {
        populateSchemaRequestFields(schema);
        if (!schemaService.schemaExists(schema.getId())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            schemaValidationService.validate(schema);
        } catch (JsonSchemaServiceException e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(schemaService.updateSchema(schema), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<JsonSchema> deleteSchema(@RequestParam("id") String id) {
        if (!schemaService.schemaExists(id)) {
            return ResponseEntity.badRequest().build();
        }

        schemaService.deleteSchema(id);
        return ResponseEntity.ok().build();
    }

    private void populateSchemaRequestFields(JsonSchema schema) {
        JsonNode node = schema.getSchema();
        String schemaId = Objects.requireNonNull(node.get("$id"), "$id field cannot be null!").asText();
        schema.setId(schemaId);
        schema.setName(schemaIdExtractor.getSchemaName(schemaId));
        schema.setVersion(schemaIdExtractor.getVersion(schemaId));
        schema.setTitle(node.get("title").asText());
        schema.setDescription(node.get("description").asText());
    }
}
