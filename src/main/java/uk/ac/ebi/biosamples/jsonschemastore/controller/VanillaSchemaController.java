package uk.ac.ebi.biosamples.jsonschemastore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;

@Slf4j
@RestController
@RequestMapping(value = "/registry/schemas", produces = {"application/json"})
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

    @GetMapping("/{accession}")
    public ResponseEntity<JsonNode> getSchemaByAccession(@PathVariable("accession") String accession) {
        return schemaService.getLatestSchemaByAccession(accession)
                .map(r -> ResponseEntity.ok(r.getSchema()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{accession}/{version}")
    public ResponseEntity<JsonNode> getSchemaByAccessionAndVersion(@PathVariable("accession") String accession,
                                                              @PathVariable("version") String version) {
        return schemaService.getSchemaByAccessionAndVersion(accession, version)
                .map(r -> ResponseEntity.ok(r.getSchema()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
