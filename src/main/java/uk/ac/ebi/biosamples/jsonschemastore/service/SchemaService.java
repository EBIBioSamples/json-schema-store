package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;
import uk.ac.ebi.biosamples.jsonschemastore.util.MongoModelConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SchemaService {
    private final AccessioningService accessioningService;
    private final SchemaRepository schemaRepository;
    private final MongoModelConverter modelConverter;

    public SchemaService(AccessioningService accessioningService, SchemaRepository schemaRepository, MongoModelConverter modelConverter) {
        this.accessioningService = accessioningService;
        this.schemaRepository = schemaRepository;
        this.modelConverter = modelConverter;
    }

    public Optional<JsonSchema> getSchemaByNameAndVersion(@NonNull String schemaName, String version) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findFirstByNameAndVersionOrderByVersionDesc(schemaName, version);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Optional<JsonSchema> getLatestSchemaByAccession(@NonNull String accession) {
        return schemaRepository.findFirstByAccessionOrderByVersionDesc(accession)
                .map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Optional<JsonSchema> getSchemaByAccessionAndVersion(@NonNull String accession, String version) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findFirstByAccessionAndVersionOrderByVersionDesc(accession, version);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Optional<JsonSchema> getSchemaById(@NonNull String id) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findById(id);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Page<SchemaOutline> getAllVersionsByAccession(@NonNull String accession, int page, int size) {
        Page<MongoJsonSchema> mongoSchemas = schemaRepository.findByAccessionOrderByVersionDesc(accession, PageRequest.of(page, size));

        List<SchemaOutline> schemas = mongoSchemas.stream()
                .map(modelConverter::mongoJsonSchemaToSchemaOutline)
                .collect(Collectors.toList());
        return new PageImpl<>(schemas, PageRequest.of(page, size), mongoSchemas.getTotalElements());
    }

    public Page<JsonSchema> getSchemaPage(String text, int page, int size) {
        Page<MongoJsonSchema> mongoSchemas;
        if (text.isEmpty()) {
            mongoSchemas = schemaRepository.findAll(PageRequest.of(page, size));
        } else {
            TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(text);
            mongoSchemas = schemaRepository.findAllBy(textCriteria, PageRequest.of(page, size));
        }

        List<JsonSchema> schemas = mongoSchemas.stream()
                .map(modelConverter::mongoJsonSchemaToJsonSchema)
                .collect(Collectors.toList());
        return new PageImpl<>(schemas, PageRequest.of(page, size), mongoSchemas.getTotalElements());
    }

    public Page<SchemaOutline> getSchemaList(int page, int size) {
        Page<MongoJsonSchema> mongoSchemas = schemaRepository.findAll(PageRequest.of(page, size));

        List<SchemaOutline> schemas = mongoSchemas.stream()
                .map(modelConverter::mongoJsonSchemaToSchemaOutline)
                .collect(Collectors.toList());
        return new PageImpl<>(schemas, PageRequest.of(page, size), mongoSchemas.getTotalElements());
    }

    public JsonSchema saveSchema(@NonNull JsonSchema jsonSchema) {
        String accession = jsonSchema.getAccession();
        if (accession == null || accession.isEmpty()) {
            populateAccession(jsonSchema);
        }

        MongoJsonSchema mongoJsonSchema = modelConverter.jsonSchemaToMongoJsonSchema(jsonSchema);
        MongoJsonSchema mongoJsonSchemaResult = schemaRepository.save(mongoJsonSchema);
        return modelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaResult);
    }

    // todo check accession logic in both POST and PUT requests
    public JsonSchema saveSchemaWithAccession(@NonNull JsonSchema jsonSchema) {
        String accession = jsonSchema.getAccession();
        if (accession != null && !accession.isEmpty()) {
            jsonSchema.setAccession(accession);
        }
        MongoJsonSchema mongoJsonSchema = modelConverter.jsonSchemaToMongoJsonSchema(jsonSchema);
        MongoJsonSchema mongoJsonSchemaResult = schemaRepository.save(mongoJsonSchema);
        return modelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaResult);
    }

    public void deleteSchema(@NonNull String schemaId) {
        schemaRepository.deleteById(schemaId);
    }

    public boolean schemaIdExists(String schemaId) {
        Optional<MongoJsonSchema> schema = schemaRepository.findById(schemaId);
        return schema.isPresent();
    }

    public boolean schemaAccessionExists(String accession) {
        Optional<MongoJsonSchema> schema = schemaRepository.findFirstByAccessionOrderByVersionDesc(accession);
        return schema.isPresent();
    }

    public boolean schemaDomainAndNameExists(String domain, String name) {
        Optional<MongoJsonSchema> schema = schemaRepository.findFirstByDomainAndNameOrderByVersionDesc(domain, name);
        return schema.isPresent();
    }

    private void populateAccession(JsonSchema jsonSchema) {
        String accession = accessioningService.getSchemaAccession(jsonSchema.getId());
        jsonSchema.setAccession(accession);
    }

    public Page<MongoJsonSchema> findBySchema(MongoJsonSchema exampleSchema, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("schemaFieldAssociations")
                .withMatcher("l", new ExampleMatcher.GenericPropertyMatcher());
        Example<MongoJsonSchema> example = Example.of(exampleSchema, matcher);
        return schemaRepository.findAll(example, pageable);
    }
}
