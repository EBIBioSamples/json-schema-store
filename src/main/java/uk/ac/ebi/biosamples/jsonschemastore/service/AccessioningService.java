package uk.ac.ebi.biosamples.jsonschemastore.service;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.exception.ApplicationStateException;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;

@Slf4j
@Service
public class AccessioningService {
    private final SchemaRepository schemaRepository;

    public AccessioningService(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    public String getSchemaAccession(final String schemaId) {
        return schemaRepository.findById(schemaId)
                .map(MongoJsonSchema::getAccession)
                .orElseGet(() -> createNewAccession(schemaId, 0));
    }

    //handle distributed accession generation
    private String createNewAccession(String schemaId, int retries) {
        String latestAccession = schemaRepository.findFirstByOrderByAccessionDesc()
                .map(MongoJsonSchema::getAccession)
                .orElse("ERC100001");
        String accession = incrementAccession(latestAccession);
        MongoJsonSchema mongoJsonSchema = new MongoJsonSchema();
        mongoJsonSchema.setId(schemaId);
        mongoJsonSchema.setAccession(accession);
        try {
            schemaRepository.insert(mongoJsonSchema);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                log.warn("Failed to insert document with new accession: {}, retrying..", accession);
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
        int accessionNumber = Integer.parseInt(accession.split("ERC")[1]);
        if (accessionNumber < 100001 || accessionNumber > 199999) {//Assigned range of ERC IDs ERC100001-ERC199999
            throw new ApplicationStateException("Accession out of bound, should be in the range ERC100001-ERC199999");
        }
        return "ERC" + ++accessionNumber;
    }
}
