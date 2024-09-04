package uk.ac.ebi.biosamples.jsonschemastore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

import java.util.List;
import java.util.Optional;

public interface SchemaRepository extends MongoRepository<MongoJsonSchema, String> {
    Page<MongoJsonSchema> findAllBy(TextCriteria criteria, Pageable pageable);
    @Query("{ $text: { $search: ?0 } }")
    Page<MongoJsonSchema> findAllByText(String text, Pageable pageable);

    @Query("{\n" +
            "    $or: [\n" +
            "        { \"accession\": { $regex: /?0/, $options: \"i\" } },\n" +
            "        { \"description\": { $regex: /?0/, $options: \"i\" } },\n" +
            "        { \"title\": { $regex: /?0/, $options: \"i\" } }\n" +
            "        { \"name\": { $regex: /?0/, $options: \"i\" } }\n" +
            "    ]\n" +
            "}")
    Page<MongoJsonSchema> findAllByTextPartial(String text, Pageable pageable);

    List<MongoJsonSchema> findByIdIn(List<String> ids);
    Page<MongoJsonSchema> findByNameOrderByVersionDesc(String schemaName, Pageable pageable);

    Page<MongoJsonSchema> findByAccessionOrderByVersionDesc(String accession, Pageable pageable);

    Optional<MongoJsonSchema> findFirstByNameOrderByVersionDesc(String schemaName);

    Optional<MongoJsonSchema> findFirstByNameAndVersionOrderByVersionDesc(String schemaName, String version);

    Optional<MongoJsonSchema> findFirstByOrderByAccessionDesc();

    Optional<MongoJsonSchema> findFirstByAuthorityOrderByAccessionDesc(String authority);

    Optional<MongoJsonSchema> findFirstByAccessionAndVersionOrderByVersionDesc(String accession, String version);

    Optional<MongoJsonSchema> findFirstByAccessionOrderByVersionDesc(String accession);

    Optional<MongoJsonSchema> findFirstByDomainAndNameOrderByVersionDesc(String domain, String name);

}
