package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;

import java.util.List;

@RepositoryRestResource(collectionResourceDescription = @Description("hi"))
public interface FieldRepository
        extends MongoRepository<Field, String> {
    Field findByLabel(String label);

    /**
     * searches in the {@link Field#usedBySchemas} list
     * @param schemaId in name:version format
     * @return
     */
    List<Field> findByUsedBySchemas(String schemaId);


    @Override
    @RestResource(exported = true, rel = "fff")
    List<Field> findAllById(Iterable<String> ids);

    List<Field> findByIdIn(List<String> ids);
}



