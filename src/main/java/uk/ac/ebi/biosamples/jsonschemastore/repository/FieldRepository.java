package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;

import java.util.List;

@RepositoryRestResource
public interface FieldRepository extends MongoRepository<Field, String> {
    Field findByLabel(String label);
    List<Field> findByUsedBySchemas(String schemaId);

}
