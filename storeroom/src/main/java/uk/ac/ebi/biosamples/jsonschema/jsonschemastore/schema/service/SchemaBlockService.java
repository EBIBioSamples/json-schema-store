package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.List;

@Service
public class SchemaBlockService {

    private final SchemaBlockRepository schemaBlockRepository;

    public SchemaBlockService(SchemaBlockRepository schemaBlockRepository) {
        this.schemaBlockRepository = schemaBlockRepository;
    }

    public SchemaBlock createSchemaBlock(SchemaBlock schemaBlock) {
        return schemaBlockRepository.save(schemaBlock);
    }

    public List<SchemaBlock> getAllSchemaBlocks() {
        return schemaBlockRepository.findAll();
    }
}
