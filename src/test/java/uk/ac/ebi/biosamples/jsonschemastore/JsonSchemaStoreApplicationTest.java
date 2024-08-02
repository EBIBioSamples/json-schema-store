package uk.ac.ebi.biosamples.jsonschemastore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

@AutoConfigureDataMongo
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class JsonSchemaStoreApplicationTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    void contextLoads() {

    }
}
