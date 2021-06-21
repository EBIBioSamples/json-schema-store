package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

import java.util.Optional;

public interface SchemaRepository extends MongoRepository<MongoJsonSchema, String> {
    Page<MongoJsonSchema> findAllBy(TextCriteria criteria, Pageable pageable);

    Page<MongoJsonSchema> findByNameOrderByVersionDesc(String schemaName, Pageable pageable);

    Page<MongoJsonSchema> findByAccessionOrderByVersionDesc(String accession, Pageable pageable);

    Optional<MongoJsonSchema> findFirstByNameOrderByVersionDesc(String schemaName);

    Optional<MongoJsonSchema> findFirstByNameAndVersionOrderByVersionDesc(String schemaName, String version);

    Optional<MongoJsonSchema> findFirstByOrderByAccessionDesc();

    Optional<MongoJsonSchema> findFirstByAccessionAndVersionOrderByVersionDesc(String accession, String version);

    Optional<MongoJsonSchema> findFirstByAccessionOrderByVersionDesc(String accession);

    Optional<MongoJsonSchema> findFirstByDomainAndNameOrderByVersionDesc(String domain, String name);
}
