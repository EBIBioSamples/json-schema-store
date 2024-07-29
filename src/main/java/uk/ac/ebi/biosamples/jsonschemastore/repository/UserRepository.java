package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import uk.ac.ebi.biosamples.jsonschemastore.model.User;

import java.util.Optional;


public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);

  @Query("{username:'?0'}")
  User findUserByUsername(String username);
}
