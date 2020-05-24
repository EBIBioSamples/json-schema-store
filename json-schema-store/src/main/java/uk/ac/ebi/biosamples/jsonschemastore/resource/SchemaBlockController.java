package uk.ac.ebi.biosamples.jsonschemastore.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaBlockRepository;

import java.util.List;
import java.util.Optional;

@RestController
public class SchemaBlockController {

  @Autowired
  private SchemaBlockRepository schemaBlockRepository;

  @PostMapping("/addSchema")
  public SchemaBlock addSchemaBlock(@RequestBody SchemaBlock schemaBlock) {
    return schemaBlockRepository.save(schemaBlock);
  }

  @GetMapping("/getSchemas")
  public List<SchemaBlock> getSchemaBlocks() {
    return schemaBlockRepository.findAll();
  }

  @GetMapping("/findById/{id}")
  public Optional<SchemaBlock> getSchemaBlock(@PathVariable int id) {
    return schemaBlockRepository.findById(id);
  }

  @DeleteMapping("/deleteSchemas/{id}")
  public String deleteSchemaBlock(@PathVariable int id) {
    schemaBlockRepository.deleteById(id);
    return "deleted";
  }
}
