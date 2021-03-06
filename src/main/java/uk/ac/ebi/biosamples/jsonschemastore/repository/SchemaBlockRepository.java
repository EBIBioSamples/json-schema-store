package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.biosamples.jsonschemastore.document.SchemaBlock;

import java.util.Optional;

public interface SchemaBlockRepository extends MongoRepository<SchemaBlock, String> {
  Page<SchemaBlock> findAllBy(TextCriteria criteria, Pageable pageable);

  Page<SchemaBlock> findAllByLatestTrue(Pageable pageable);

  Page<SchemaBlock> findBySchemaNameOrderByVersionDesc(String schemaName, Pageable pageable);

  Optional<SchemaBlock> findFirstBySchemaNameOrderByVersionDesc(String schemaName);

  Optional<SchemaBlock> findFirstBySchemaNameAndVersionOrderByVersionDesc(String schemaName, String version);

}
