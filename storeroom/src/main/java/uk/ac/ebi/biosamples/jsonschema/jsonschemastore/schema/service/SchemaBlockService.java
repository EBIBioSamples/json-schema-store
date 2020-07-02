package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SchemaBlockService {

  private final SchemaBlockRepository schemaBlockRepository;
  private final ModelMapper modelMapper;

  public SchemaBlockService(SchemaBlockRepository schemaBlockRepository, ModelMapper modelMapper) {
    this.schemaBlockRepository = schemaBlockRepository;
    this.modelMapper = modelMapper;
    this.modelMapper.getConfiguration()
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
  }

  public SchemaBlockDocument createSchemaBlock(@NonNull SchemaBlockDocument schemaBlockDocument) {
    SchemaBlock schemaBlock = modelMapper.map(schemaBlockDocument, SchemaBlock.class);
    return modelMapper.map(schemaBlockRepository.insert(schemaBlock), SchemaBlockDocument.class);
  }

  public Optional<SchemaBlockDocument> getAllSchemaBlocksById(@NonNull String id) {
    return schemaBlockRepository.findById(id)
            .map(schemaBlock -> Optional.of(modelMapper.map(schemaBlock, SchemaBlockDocument.class))
                    .orElseGet(null));
  }

  public List<SchemaBlockDocument> getAllSchemaBlocks() {
    return modelMapper.map(
        schemaBlockRepository.findAll(), new TypeToken<List<SchemaBlockDocument>>() {}.getType());
  }

  public void deleteSchemaBlocks(@NonNull SchemaBlockDocument schemaBlockDocument) {
    SchemaBlock schemaBlock = modelMapper.map(schemaBlockDocument, SchemaBlock.class);
    schemaBlockRepository.delete(schemaBlock);
  }

  public void deleteSchemaBlocksById(@NonNull String id) {
    schemaBlockRepository.deleteById(id);
  }

  public SchemaBlockDocument updateSchemaBlocks(SchemaBlockDocument schemaBlockDocument)
      throws JsonSchemaServiceException {
    // TODO: enable versioning
    if (schemaBlockRepository.existsById(schemaBlockDocument.getId())) {
      SchemaBlock schemaBlock = modelMapper.map(schemaBlockDocument, SchemaBlock.class);
      return modelMapper.map(schemaBlockRepository.save(schemaBlock), SchemaBlockDocument.class);
    } else {
      String errorMessage = "Provided schemaBlockDocument does not exists!";
      log.error(errorMessage);
      throw new JsonSchemaServiceException(errorMessage);
    }
  }
}
