package uk.ac.ebi.biosamples.jsonschemastore.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.exception.MalformedSchemaException;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaValidationService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaObjectPopulator;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaResourceAssembler;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "/checklist/converter", produces = {"application/json"})
public class ChecklistConverterController {
    private final ChecklistConverterService checklistConverterService;

    public ChecklistConverterController(ChecklistConverterService checklistConverterService) {
        this.checklistConverterService = checklistConverterService;
    }

    @GetMapping("/xml/{checklist}")
    public ResponseEntity<EnaChecklist> convertChecklist(@PathVariable("checklist") String checklist) {
        EnaChecklist enaChecklist = checklistConverterService.getChecklist(checklist);
        return new ResponseEntity<>(enaChecklist, HttpStatus.OK);
    }

    @GetMapping("/{checklist}")
    public ResponseEntity<String> convertChecklist1(@PathVariable("checklist") String checklist) {
        String enaChecklist = checklistConverterService.getEnaChecklist(checklist);
        return new ResponseEntity<>(enaChecklist, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<JsonSchema> convertAndSaveChecklist(@RequestBody JsonSchema schema) {

        return new ResponseEntity<>(null);
    }
}
