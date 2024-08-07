package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;

public interface FieldRepository extends MongoRepository<Field, String> {
    Field findByLabel(String label);
}
