package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.exception.MalformedSchemaException;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaValidationService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaObjectPopulator;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaResourceAssembler;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(value = "${spring.data.rest.basePath}/schemas", produces = {"application/json"})
@RequiredArgsConstructor
public class SchemaController {
    private final SchemaService schemaService;
    private final SchemaValidationService schemaValidationService;
    private final SchemaResourceAssembler schemaResourceAssembler;
    private final SchemaObjectPopulator schemaObjectPopulator;
    private final PagedResourcesAssembler pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<JsonSchema> getSchema(@RequestParam("id") String id) {
        return schemaService.getSchemaById(id)
                .map(schemaResourceAssembler::populateResources)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{accession}/{version}")
    public ResponseEntity<JsonSchema> getSchemaByAccessionAndVersion(@PathVariable("accession") String accession,
                                                                     @PathVariable("version") String version) {
        return schemaService.getSchemaByAccessionAndVersion(accession, version)
                .map(schemaResourceAssembler::populateResources)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public PagedModel<EntityModel<JsonSchema>> getSchemaPage(@RequestParam(value = "text", required = false) String text,
                                                             @RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "size", required = false) Integer size) {
        text = Objects.requireNonNullElse(text, "");
        page = Objects.requireNonNullElse(page, 0);
        size = Objects.requireNonNullElse(size, 10);
        Page<JsonSchema> schemaPage = schemaService.getSchemaPage(text, page, size);
        return schemaResourceAssembler.buildPage(schemaPage);
    }

    @GetMapping("/list")
    public PagedModel<EntityModel<SchemaOutline>> getSchemaList(@RequestParam(value = "page", required = false) Integer page,
                                                                @RequestParam(value = "size", required = false) Integer size) {
        page = Objects.requireNonNullElse(page, 0);
        size = Objects.requireNonNullElse(size, 100);
        Page<SchemaOutline> schemaPage = schemaService.getSchemaList(page, size);
        return schemaResourceAssembler.buildPageForSchemaOutline(schemaPage);
    }

    @PostMapping
    public ResponseEntity<JsonSchema> createSchema(@RequestBody JsonSchema schema) {
        schemaObjectPopulator.populateSchema(schema);
        if (schemaService.schemaIdExists(schema.getId())) {
            throw new MalformedSchemaException("Schema Id already exists");
        }
        if (schema.getAccession() != null && !schema.getAccession().isEmpty()) {
            throw new MalformedSchemaException("Schema accession should not be present at creation");
        }
        //todo if domain + name already exist can not create but should update
        if (schemaService.schemaDomainAndNameExists(schema.getDomain(), schema.getName())) {
            throw new MalformedSchemaException("Schema with same domain and name exists, use update endpoint to update schema");
        }

        try {
            schemaValidationService.validate(schema);
        } catch (JsonSchemaServiceException e) {
            throw new MalformedSchemaException("Validation failed: " + e.getMessage());
        }

        return new ResponseEntity<>(schemaService.saveSchema(schema), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<JsonSchema> updateSchema(@RequestParam("id") String id, @RequestBody JsonSchema schema) {
        schemaObjectPopulator.populateSchema(schema);
        if (schema.getAccession() == null
                || schema.getAccession().isEmpty()
                || !schemaService.schemaAccessionExists(schema.getAccession())) {
            throw new MalformedSchemaException("Accession must be present to update the schema id: " + id);
        }

        if (schemaService.schemaIdExists(schema.getId())) {
            schemaObjectPopulator.incrementAndPopulateSchema(schema);
        }

        try {
            schemaValidationService.validate(schema);
        } catch (JsonSchemaServiceException e) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(schemaService.saveSchema(schema), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<JsonSchema> deleteSchema(@RequestParam("id") String id) {
        if (!schemaService.schemaIdExists(id)) {
            return ResponseEntity.badRequest().build();
        }

        schemaService.deleteSchema(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list/versions/{accession}")
    public PagedModel<EntityModel<SchemaOutline>> getSchemaList(@PathVariable(value = "accession") String accession) {
        int page = 0;
        int size = 10;
        Page<SchemaOutline> schemaPage = schemaService.getAllVersionsByAccession(accession, page, size);
        return schemaResourceAssembler.buildPageForSchemaOutline(schemaPage);
    }

    @GetMapping(value = "/search/findByExample")
    public PagedModel<EntityModel<MongoJsonSchema>>
        findByExample(@ModelAttribute MongoJsonSchema exampleSchema,
                      Pageable pageable) {
        Page<MongoJsonSchema> entityPage = schemaService.findBySchema(exampleSchema, pageable);
        return pagedResourcesAssembler.toModel(entityPage);

    }



}
