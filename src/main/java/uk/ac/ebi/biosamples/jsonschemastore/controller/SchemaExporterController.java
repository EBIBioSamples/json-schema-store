package uk.ac.ebi.biosamples.jsonschemastore.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.service.JsonSchemaExporter;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;

@Slf4j
@RestController
@RequestMapping(value = "/exporter/schemas", produces = {"application/json"})
@RequiredArgsConstructor
public class SchemaExporterController {
  private final JsonSchemaExporter jsonSchemaExporter;
  private final SchemaService schemaService;

  @GetMapping("/{accessionOrId}")
  public ResponseEntity<JsonNode> getSchemaLatestByAccessionOrById(@PathVariable("accessionOrId") String accessionOrId) {
    return schemaService.getSchemaByIdOrLatestByAccession(accessionOrId)
        .map(jsonSchema -> ResponseEntity.ok(jsonSchema.getSchema()))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/{accession}/{version}")
  public ResponseEntity<JsonNode> getSchemaByAccessionAndVersion(@PathVariable("accession") String accession,
                                                                 @PathVariable("version") String version) {
    return schemaService.getSchemaByAccessionAndVersion(accession, version)
        .map(r -> ResponseEntity.ok(r.getSchema()))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  // This endpoint returns schema dynamically generated from `fields`
  public ResponseEntity<String> getSchemaById(@RequestParam("id") String id) {
    return ResponseEntity.ok(jsonSchemaExporter.generateJsonSchemaFromChecklistFields(id));
  }

}
