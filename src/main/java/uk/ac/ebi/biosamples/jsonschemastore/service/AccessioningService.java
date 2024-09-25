package uk.ac.ebi.biosamples.jsonschemastore.service;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.exception.ApplicationStateException;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessioningService {
    private final SchemaRepository schemaRepository;
    private final SchemaStoreProperties properties;

    public String getSchemaAccession(final String schemaId) {
        return schemaRepository.findById(schemaId)
                .map(MongoJsonSchema::getAccession)
                .orElseGet(() -> createNewAccession(schemaId, 0));
    }

    //handle distributed accession generation
    private String createNewAccession(String schemaId, int retries) {
        String latestAccession = schemaRepository.findFirstByAuthorityOrderByAccessionDesc(properties.getDefaultAuthority())
                .map(MongoJsonSchema::getAccession)
                .orElse("BSDC00000");
        String accession = incrementAccession(latestAccession);
        MongoJsonSchema mongoJsonSchema = new MongoJsonSchema();
        mongoJsonSchema.setId(schemaId);
        mongoJsonSchema.setAccession(accession);
        mongoJsonSchema.setVersion("1.0");
        try {
            schemaRepository.insert(mongoJsonSchema);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                log.warn("Failed to insert document with new accession: {}, retrying...", accession);
                if (retries > 2) {
                    throw new ApplicationStateException("Could not allocate accession for new Schema, Please try again later");
                }
                retries++;
                accession = createNewAccession(schemaId, retries);
            } else {
                throw e;
            }
        }

        return accession;
    }

    private String incrementAccession(String accession) {
        int accessionNumber = Integer.parseInt(accession.split("BSDC")[1]);
        return "BSDC" + String.format("%05d", ++accessionNumber);
    }
}
