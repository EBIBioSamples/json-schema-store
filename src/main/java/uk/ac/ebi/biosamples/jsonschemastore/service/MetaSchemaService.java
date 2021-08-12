package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoMetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.MetaSchemaRepository;
import uk.ac.ebi.biosamples.jsonschemastore.util.MongoModelConverter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetaSchemaService {
    private final MetaSchemaRepository metaSchemaRepository;
    private final MongoModelConverter modelConverter;

    public MetaSchemaService(MetaSchemaRepository metaSchemaRepository, MongoModelConverter modelConverter) {
        this.metaSchemaRepository = metaSchemaRepository;
        this.modelConverter = modelConverter;
    }

    public Optional<MetaSchema> getSchemaById(@NonNull String id) {
        Optional<MongoMetaSchema> optionalSchema = metaSchemaRepository.findById(id);
        return optionalSchema.map(modelConverter::mongoMetaSchemaToMetaSchema);
    }

    public Page<MetaSchema> getSchemaPage(int page, int size) {
        Page<MongoMetaSchema> mongoSchemas = metaSchemaRepository.findAll(PageRequest.of(page, size));
        List<MetaSchema> schemas = mongoSchemas.stream()
                .map(modelConverter::mongoMetaSchemaToMetaSchema)
                .collect(Collectors.toList());
        return new PageImpl<>(schemas, PageRequest.of(page, size), mongoSchemas.getTotalElements());
    }

    public MetaSchema createSchema(@NonNull MetaSchema metaSchema) {
        MongoMetaSchema mongoJsonSchema = modelConverter.metaSchemaToMongoMetaSchema(metaSchema);
        MongoMetaSchema mongoJsonSchemaResult = metaSchemaRepository.insert(mongoJsonSchema);
        return modelConverter.mongoMetaSchemaToMetaSchema(mongoJsonSchemaResult);
    }


    public boolean schemaExists(String schemaId) {
        Optional<MongoMetaSchema> schema = metaSchemaRepository.findById(schemaId);
        return schema.isPresent();
    }
}
