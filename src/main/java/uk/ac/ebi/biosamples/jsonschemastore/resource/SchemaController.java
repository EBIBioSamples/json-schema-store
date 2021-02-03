package uk.ac.ebi.biosamples.jsonschemastore.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaValidationService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaObjectPopulator;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaResourceAssembler;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v2/schemas")
public class SchemaController {
    private final SchemaService schemaService;
    private final SchemaValidationService schemaValidationService;
    private final SchemaResourceAssembler schemaResourceAssembler;

    public SchemaController(SchemaService schemaService, SchemaValidationService schemaValidationService,
                            SchemaResourceAssembler schemaResourceAssembler) {
        this.schemaService = schemaService;
        this.schemaValidationService = schemaValidationService;
        this.schemaResourceAssembler = schemaResourceAssembler;
    }

    @GetMapping
    public ResponseEntity<JsonSchema> getSchema(@RequestParam("id") String id) {
        return schemaService.getSchemaById(id)
                .map(schemaResourceAssembler::populateResources)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{version}/{schemaName}")
    public ResponseEntity<JsonSchema> getSchemaWithVersion(@PathVariable("schemaName") String schemaName,
                                                           @PathVariable("version") String version) {
        return schemaService.getSchemaByNameAndVersion(schemaName, version)
                .map(schemaResourceAssembler::populateResources)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public PagedModel<EntityModel<JsonSchema>> getSchemaPage1(@RequestParam(value = "text", required = false) String text,
                                                              @RequestParam(value = "page", required = false) Integer page,
                                                              @RequestParam(value = "size", required = false) Integer size) {
        text = Objects.requireNonNullElse(text, "");
        page = Objects.requireNonNullElse(page, 0);
        size = Objects.requireNonNullElse(size, 10);
        Page<JsonSchema> schemaPage = schemaService.getSchemaPage(text, page, size);
        return schemaResourceAssembler.buildPage(schemaPage);
    }

    @GetMapping("/list")
    public PagedModel<EntityModel<SchemaOutline>> getSchemaList(@RequestParam(value = "page", required = false) Integer page,
                                                                @RequestParam(value = "size", required = false) Integer size) {
        page = Objects.requireNonNullElse(page, 0);
        size = Objects.requireNonNullElse(size, 100);
        Page<SchemaOutline> schemaPage = schemaService.getSchemaList(page, size);
        return schemaResourceAssembler.buildPageForSchemaOutline(schemaPage);
    }

    @PostMapping
    public ResponseEntity<JsonSchema> createSchema(@RequestBody JsonSchema schema) {
        SchemaObjectPopulator.populateSchemaRequestFields(schema);
        if (schemaService.schemaExists(schema.getId())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            schemaValidationService.validate(schema);
        } catch (JsonSchemaServiceException e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(schemaService.saveSchema(schema), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<JsonSchema> updateSchema(@RequestParam("id") String id, @RequestBody JsonSchema schema) {
        SchemaObjectPopulator.populateSchemaRequestFields(schema);
        if (!schemaService.schemaExists(schema.getId())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            schemaValidationService.validate(schema);
        } catch (JsonSchemaServiceException e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(schemaService.saveSchema(schema), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<JsonSchema> deleteSchema(@RequestParam("id") String id) {
        if (!schemaService.schemaExists(id)) {
            return ResponseEntity.badRequest().build();
        }

        schemaService.deleteSchema(id);
        return ResponseEntity.ok().build();
    }
}
