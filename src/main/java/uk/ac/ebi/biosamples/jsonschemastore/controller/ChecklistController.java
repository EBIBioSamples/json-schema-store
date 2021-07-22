package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.exception.MalformedSchemaException;
import uk.ac.ebi.biosamples.jsonschemastore.model.Checklist;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.service.ChecklistService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaValidationService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaObjectPopulator;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaResourceAssembler;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "/api/v2/checklist", produces = {"application/json"})
public class ChecklistController {
    private final SchemaService schemaService;
    private final SchemaValidationService schemaValidationService;
    private final SchemaResourceAssembler schemaResourceAssembler;
    private final ChecklistService checklistService;
    private final SchemaObjectPopulator populator;

    public ChecklistController(SchemaService schemaService, SchemaValidationService schemaValidationService,
                               SchemaResourceAssembler schemaResourceAssembler, SchemaObjectPopulator populator,
                               ChecklistService checklistService) {
        this.schemaService = schemaService;
        this.schemaValidationService = schemaValidationService;
        this.schemaResourceAssembler = schemaResourceAssembler;
        this.checklistService = checklistService;
        this.populator = populator;
    }

    @GetMapping
    public ResponseEntity<Checklist> getChecklist(@RequestParam("id") String id) {
        /*return checklistService.getChecklist(id)
                .map(schemaResourceAssembler::populateResources)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());*/
        return null;
    }

    @PostMapping
    public ResponseEntity<String> createSchema(@RequestBody Checklist checklist) {
        String jsonSchema = checklistService.checklistToSchema(checklist);
        return new ResponseEntity<>(jsonSchema, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Checklist> updateSchema(@RequestParam("id") String id, @RequestBody Checklist checklist) {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Checklist> deleteSchema(@RequestParam("id") String id) {

        return ResponseEntity.ok().build();
    }
}
