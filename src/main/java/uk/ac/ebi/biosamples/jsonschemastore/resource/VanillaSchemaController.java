package uk.ac.ebi.biosamples.jsonschemastore.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;

@Slf4j
@RestController
@RequestMapping("/registry/schemas")
public class VanillaSchemaController {
    private SchemaService schemaService;

    public VanillaSchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @GetMapping
    public ResponseEntity<JsonNode> getSchemaById(@RequestParam("id") String id) {
        return schemaService.getSchemaById(id)
                .map(r -> ResponseEntity.ok(r.getSchema()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{schemaName}")
    public ResponseEntity<JsonNode> getSchemaByName(@PathVariable("schemaName") String schemaName,
                                                    @RequestParam(value = "version", required = false) String version) {
        return schemaService.getSchemaByNameAndVersion(schemaName, version)
                .map(r -> ResponseEntity.ok(r.getSchema()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{version}/{schemaName}")
    public ResponseEntity<JsonNode> getSchemaByNameAndVersion(@PathVariable("schemaName") String schemaName,
                                                              @PathVariable("version") String version) {
        return schemaService.getSchemaByNameAndVersion(schemaName, version)
                .map(r -> ResponseEntity.ok(r.getSchema()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
