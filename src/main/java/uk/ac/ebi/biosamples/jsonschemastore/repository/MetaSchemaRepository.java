package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoMetaSchema;

import java.util.Optional;

public interface MetaSchemaRepository extends MongoRepository<MongoMetaSchema, String> {
    Page<MongoMetaSchema> findAllBy(TextCriteria criteria, Pageable pageable);

    Page<MongoMetaSchema> findByNameOrderByVersionDesc(String schemaName, Pageable pageable);

    Optional<MongoMetaSchema> findFirstByNameOrderByVersionDesc(String schemaName);

    Optional<MongoMetaSchema> findFirstByNameAndVersionOrderByVersionDesc(String schemaName, String version);
}
