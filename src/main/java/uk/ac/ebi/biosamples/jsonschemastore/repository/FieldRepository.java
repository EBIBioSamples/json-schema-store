package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceDescription = @Description("checklist fields"))
public interface FieldRepository extends MongoRepository<Field, String> {
    Field findByLabel(String label);

    /**
     * searches in the {@link Field}#usedBySchemas list
     * @param schemaId in name:version format
     * @return
     */
    @RestResource(exported = false)
    List<Field> findByUsedBySchemas(String schemaId);

    Page<Field> findByUsedBySchemas(String schemaId, Pageable pageable);

    @Override
    List<Field> findAllById(Iterable<String> ids);

    Page<Field> findAllByIdIn(List<String> ids, Pageable pageable);

    List<Field> findAllByNameIn(List<String> names);

    @Query("{ $text: { $search: ?0 } }")
    Page<Field> findAllByText(String text, Pageable pageable);
    @Query("{\n" +
            "    $or: [\n" +
            "        { \"label\": { $regex: /?0/, $options: \"i\" } },\n" +
            "        { \"description\": { $regex: /?0/, $options: \"i\" } }\n" +
            "        { \"group\": { $regex: /?0/, $options: \"i\" } }\n" +
            "        { \"type\": { $regex: /?0/, $options: \"i\" } }\n" +
            "    ]\n" +
            "}")
    Page<Field> findAllByTextPartial(String text, Pageable pageable);

    Optional<Field> findByName(String name);

    record AttributeResult(String attributeName) {}

    @Aggregation(pipeline = {
            "{ '$group': { '_id': '$?0'} }",
            "{ '$sort': { '_id': 1 } }",
            "{ '$project': {'attributeName': '$_id', _id: 0 } }"
    })
    @RestResource(exported = false)
    List<AttributeResult> findAttributeValues(String attributeName);

}



