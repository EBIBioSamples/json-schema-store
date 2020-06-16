package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;

public interface SchemaBlockRepository extends MongoRepository<SchemaBlock, String> {}
