package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldGroup;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface FieldGroupRepository extends MongoRepository<FieldGroup, String> {
  Optional<FieldGroup> findByName(String name);

  Page<FieldGroup> findAllByIdIn(List<String> ids, Pageable pageable);

  @Query("{\n" +
          "    $or: [\n" +
          "        { \"name\": { $regex: /?0/, $options: \"i\" } },\n" +
          "        { \"description\": { $regex: /?0/, $options: \"i\" } }\n" +
          "    ]\n" +
          "}")
  Page<FieldGroup> findAllByTextPartial(String text, Pageable pageable);
}
