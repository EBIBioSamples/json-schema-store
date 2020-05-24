package uk.ac.ebi.biosamples.jsonschemastore.schema.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.schema.service.SchemaBlockService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class SchemaBlockController {

  private final SchemaBlockService schemaBlockService;

  public SchemaBlockController(SchemaBlockService schemaBlockService) {
    this.schemaBlockService = schemaBlockService;
  }

  @GetMapping("/schemas")
  public ResponseEntity<Object> getAllSchemaBlock() {
    List<SchemaBlock> schemaBlocks = schemaBlockService.getAllSchemaBlocks();
    return ResponseEntity.ok(schemaBlocks);
  }

  @PostMapping("/schemas")
  public ResponseEntity<Object> createSchemaBlock(@RequestBody SchemaBlock schema) {
    SchemaBlock schemaBlock = schemaBlockService.createSchemaBlock(schema);
    return new ResponseEntity<>(schemaBlock, HttpStatus.CREATED);
  }
}
