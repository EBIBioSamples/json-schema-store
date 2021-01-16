package uk.ac.ebi.biosamples.jsonschemastore.schema.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.schema.service.SchemaBlockService;

@Slf4j
@RestController
@RequestMapping("/registry/schemas")
public class VanillaSchemaController {
    private SchemaBlockService schemaBlockService;

    public VanillaSchemaController(SchemaBlockService schemaBlockService) {
        this.schemaBlockService = schemaBlockService;
    }

    @GetMapping("/{version}/{schemaName}")
    public ResponseEntity<SchemaBlockDocument> getSchemaWithVersion(@PathVariable("schemaName") String schemaName,
                                                                    @PathVariable("version") String version) {
        return schemaBlockService.getSchemaByNameAndVersion(schemaName, version)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{schemaName}")
    public ResponseEntity<SchemaBlockDocument> getSchema(@PathVariable("schemaName") String schemaName,
                                                         @RequestParam("version") String version) {
        return schemaBlockService.getSchemaByNameAndVersion(schemaName, version)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
