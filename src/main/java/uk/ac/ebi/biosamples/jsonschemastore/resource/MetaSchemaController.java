package uk.ac.ebi.biosamples.jsonschemastore.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.service.MetaSchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaValidationService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaObjectPopulator;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v2/metaSchemas")
public class MetaSchemaController {
    private MetaSchemaService metaSchemaService;
    private final SchemaValidationService schemaValidationService;

    public MetaSchemaController(MetaSchemaService metaSchemaService, SchemaValidationService schemaValidationService) {
        this.metaSchemaService = metaSchemaService;
        this.schemaValidationService = schemaValidationService;
    }

//    @GetMapping
//    public ResponseEntity<JsonNode> getMetaSchema() {
//        return ResponseEntity.ok(this.metaSchemaService.getMetaSchema());
//    }

    @GetMapping
    public ResponseEntity<Page<MetaSchema>> getMetaSchemaPage(@RequestParam(value = "page", required = false) Integer page,
                                                              @RequestParam(value = "size", required = false) Integer size) {

        int effectivePage = Objects.requireNonNullElse(page, 0);
        int effectiveSize = Objects.requireNonNullElse(size, 10);
        return ResponseEntity.ok(metaSchemaService.getSchemaPage(effectivePage, effectiveSize));
    }

    @PostMapping
    public ResponseEntity<MetaSchema> createSchema(@RequestBody MetaSchema schema) {
        SchemaObjectPopulator.populateSchemaRequestFields(schema);
        if (metaSchemaService.schemaExists(schema.getId())) {
            return ResponseEntity.badRequest().build();
        }
        try {
            schemaValidationService.validate(schema);
        } catch (JsonSchemaServiceException e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(metaSchemaService.createSchema(schema), HttpStatus.CREATED);
    }
}
