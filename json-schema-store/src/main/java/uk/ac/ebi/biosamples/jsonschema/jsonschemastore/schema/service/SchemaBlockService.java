package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.List;

@Service
public class SchemaBlockService {

    private final SchemaBlockRepository schemaBlockRepository;
    private final ModelMapper modelMapper;

    public SchemaBlockService(SchemaBlockRepository schemaBlockRepository, ModelMapper modelMapper) {
        this.schemaBlockRepository = schemaBlockRepository;
        this.modelMapper = modelMapper;
    }

    public SchemaBlock createSchemaBlock(@NonNull SchemaBlockDocument schemaBlockDocument) {
        SchemaBlock schemaBlock = modelMapper.map(schemaBlockDocument, SchemaBlock.class);
        return schemaBlockRepository.save(schemaBlock);
    }

    public List<SchemaBlock> getAllSchemaBlocks() {
        return schemaBlockRepository.findAll();
    }

    public void deleteSchemaBlocks(@NonNull SchemaBlockDocument schemaBlockDocument) {
        SchemaBlock schemaBlock = modelMapper.map(schemaBlockDocument, SchemaBlock.class);
        schemaBlockRepository.delete(schemaBlock);
    }

    public void deleteSchemaBlocksById(@NonNull String id) {
        schemaBlockRepository.deleteById(id);
    }

    public void updateSchemaBlocks(SchemaBlockDocument schemaBlockDocument) {
        //TODO: enable versioning
        createSchemaBlock(schemaBlockDocument);
    }
}
