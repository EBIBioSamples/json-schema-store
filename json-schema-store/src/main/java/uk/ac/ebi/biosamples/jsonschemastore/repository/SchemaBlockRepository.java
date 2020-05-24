package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.biosamples.jsonschemastore.document.SchemaBlock;

public interface SchemaBlockRepository extends MongoRepository<SchemaBlock, Integer> {}
