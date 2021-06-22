package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AccessioningServiceTest {

//    @TestConfiguration
//    static class SchemaIdServiceTestConfiguration {
//        @MockBean
//        private SchemaRepository schemaRepository;
//        @MockBean
//        private MongoModelConverter mongoModelConverter;
//
//        @Bean
//        public SchemaIdService getSchemaIdService() {
//            return new SchemaIdService(schemaRepository, mongoModelConverter);
//        }
//    }

    @Mock
    private SchemaRepository schemaRepository;
    @Mock
    private SchemaStoreProperties properties;
    @InjectMocks
    private AccessioningService accessioningService;

    @BeforeEach
    public void setup() {
        when(schemaRepository.findFirstByOrderByAccessionDesc()).thenReturn(Optional.of(getSchema()));
        when(schemaRepository.insert(any(MongoJsonSchema.class))).thenReturn(getSchema());
        when(properties.getDefaultAuthority()).thenReturn("BIOSAMPLES");
    }

    @Test
    void getNewSchemaAccession() {
        String accession = accessioningService.getSchemaAccession("http://www.ebi.ac.uk/biosamples/schema/test/0.0.1");
        assertEquals("BSDC00001", accession);
    }

    @Test
    void getExistingSchemaAccession() {
        MongoJsonSchema testSchema = getSchema();
        when(schemaRepository.findById(testSchema.getId())).thenReturn(Optional.of(testSchema));
        String accession = accessioningService.getSchemaAccession(testSchema.getId());
        assertEquals("BSDC00002", accession);
    }

    private static MongoJsonSchema getSchema() {
        MongoJsonSchema mongoJsonSchema = new MongoJsonSchema();
        mongoJsonSchema.setId("http://www.ebi.ac.uk/biosamples/schema/test/0.0.1");
        mongoJsonSchema.setAccession("BSDC00002");
        return mongoJsonSchema;
    }
}