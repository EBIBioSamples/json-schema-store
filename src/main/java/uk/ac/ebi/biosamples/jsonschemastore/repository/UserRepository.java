package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.biosamples.jsonschemastore.model.User;

import java.util.Optional;


@RepositoryRestResource
public interface UserRepository extends MongoRepository<User, String>, CurrentUserResource {
  Optional<User> findByUsername(String username);

  @RestResource(path = "/me", rel="me")
  User findCurrentUser();
}
