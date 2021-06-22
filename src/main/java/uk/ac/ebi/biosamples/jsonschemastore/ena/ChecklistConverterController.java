package uk.ac.ebi.biosamples.jsonschemastore.ena;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        String jsonSchema = checklistConverterService.getChecklist(checklist);
        return new ResponseEntity<>(jsonSchema, HttpStatus.OK);
    }

    @PostMapping("/{checklist}")
    public ResponseEntity<String> convertAndSaveChecklist(@PathVariable("checklist") String checklist) {
        String jsonSchema = checklistConverterService.saveSchema(checklist);
        return new ResponseEntity<>(jsonSchema, HttpStatus.CREATED);
    }

    @PostMapping("/convert/all")
    public ResponseEntity<String> convertAndSaveAllEnaChecklists() {
        List<String> enaChecklists = checklistConverterService.getAndSaveAllSchema();

        return new ResponseEntity<>(enaChecklists.toString(), HttpStatus.CREATED);
    }
}
