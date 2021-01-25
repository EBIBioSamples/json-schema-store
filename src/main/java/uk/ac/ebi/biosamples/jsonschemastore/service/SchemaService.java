package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Term;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;
import uk.ac.ebi.biosamples.jsonschemastore.util.MongoModelConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SchemaService {
    private final SchemaRepository schemaRepository;
    private final MongoModelConverter modelConverter;

    public SchemaService(SchemaRepository schemaRepository, MongoModelConverter modelConverter) {
        this.schemaRepository = schemaRepository;
        this.modelConverter = modelConverter;
    }

    public Optional<JsonSchema> getSchemaByNameAndVersion(@NonNull String schemaName, String version) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findFirstByNameAndVersionOrderByVersionDesc(schemaName, version);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Optional<JsonSchema> getSchemaById(@NonNull String id) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findById(id);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
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

    public JsonSchema createSchema(@NonNull JsonSchema jsonSchema) {
        MongoJsonSchema mongoJsonSchema = modelConverter.jsonSchemaToMongoJsonSchema(jsonSchema);
        MongoJsonSchema mongoJsonSchemaResult = schemaRepository.insert(mongoJsonSchema);
        return modelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaResult);
    }

    public JsonSchema updateSchema(@NonNull JsonSchema jsonSchema) {
        MongoJsonSchema mongoJsonSchema = modelConverter.jsonSchemaToMongoJsonSchema(jsonSchema);
        MongoJsonSchema mongoJsonSchemaResult = schemaRepository.save(mongoJsonSchema);
        return modelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaResult);
    }

    public void deleteSchema(@NonNull String schemaId) {
        schemaRepository.deleteById(schemaId);
    }

    public boolean schemaExists(String schemaId) {
        Optional<MongoJsonSchema> schema = schemaRepository.findById(schemaId);
        return schema.isPresent();
    }
}
