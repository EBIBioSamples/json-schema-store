package uk.ac.ebi.biosamples.jsonschemastore.ena;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/checklist/converter", produces = {"application/json"})
public class ChecklistConverterController {
  private final ChecklistConverterService checklistConverterService;

  public ChecklistConverterController(ChecklistConverterService checklistConverterService) {
    this.checklistConverterService = checklistConverterService;
  }

  @GetMapping("/{checklist}")
  public ResponseEntity<String> convertChecklist(@PathVariable("checklist") String checklist) {
    ImportedChecklist jsonSchema = checklistConverterService.convertEnaChecklist(checklist);
    return new ResponseEntity<>(jsonSchema.getJsonSchemaString(), HttpStatus.OK);
  }

  @PutMapping("/{checklist}")
  public ResponseEntity<String> convertAndSaveChecklist(@PathVariable("checklist") String checklist) {
    String jsonSchema = checklistConverterService.persistEnaChecklist(checklist);
    return new ResponseEntity<>(jsonSchema, HttpStatus.CREATED);
  }

  @GetMapping("/convert/all")
  public ResponseEntity<String> convertAndSaveAllEnaChecklists() {
    String enaChecklists = checklistConverterService.persistEnaChecklists();

    return new ResponseEntity<>(enaChecklists, HttpStatus.CREATED);
  }
}
