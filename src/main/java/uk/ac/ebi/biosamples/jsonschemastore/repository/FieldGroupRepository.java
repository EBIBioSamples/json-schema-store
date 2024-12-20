package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldGroup;

import java.util.Optional;

@RepositoryRestResource
public interface FieldGroupRepository extends MongoRepository<FieldGroup, String> {
  Optional<FieldGroup> findByName(String name);
}
