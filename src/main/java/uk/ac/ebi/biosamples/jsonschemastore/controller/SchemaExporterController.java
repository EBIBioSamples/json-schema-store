package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.biosamples.jsonschemastore.service.JsonSchemaExporter;

@Slf4j
@RestController
@RequestMapping(value = "/exporter/schemas", produces = {"application/json"})
@RequiredArgsConstructor
public class SchemaExporterController {
  private final JsonSchemaExporter jsonSchemaExporter;

  @GetMapping
  public ResponseEntity<String> getSchemaById(@RequestParam("id") String id) {
    return ResponseEntity.ok(jsonSchemaExporter.convertEnaChecklist(id));
  }
}
