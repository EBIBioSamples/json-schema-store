package uk.ac.ebi.biosamples.jsonschemastore.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.service.MetaSchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaValidationService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaIdExtractor;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1/metaSchemas")
public class MetaSchemaController {
    private MetaSchemaService metaSchemaService;
    private final SchemaValidationService schemaValidationService;
    private final SchemaIdExtractor schemaIdExtractor;

    public MetaSchemaController(MetaSchemaService metaSchemaService, SchemaValidationService schemaValidationService,
                                SchemaIdExtractor schemaIdExtractor) {
        this.metaSchemaService = metaSchemaService;
        this.schemaValidationService = schemaValidationService;
        this.schemaIdExtractor = schemaIdExtractor;
    }

    @GetMapping
    public ResponseEntity<JsonNode> getMetaSchema() {
        return ResponseEntity.ok(this.metaSchemaService.getMetaSchema());
    }


    @PostMapping
    public ResponseEntity<MetaSchema> createSchema(@RequestBody MetaSchema schema) {
        populateSchemaRequestFields(schema);
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



    private void populateSchemaRequestFields(MetaSchema schema) {
        JsonNode node = schema.getSchema();
        String schemaId = Objects.requireNonNull(node.get("$id"), "$id field cannot be null!").asText();
        schema.setId(schemaId);
        schema.setName(schemaIdExtractor.getSchemaName(schemaId));
        schema.setVersion(schemaIdExtractor.getVersion(schemaId));
        schema.setTitle(node.get("title").asText());
        schema.setDescription(node.get("description").asText());
    }
}
