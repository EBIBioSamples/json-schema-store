package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.MetaSchemaBlock;

public interface MetaSchemaRepository extends MongoRepository<MetaSchemaBlock, String> {}
