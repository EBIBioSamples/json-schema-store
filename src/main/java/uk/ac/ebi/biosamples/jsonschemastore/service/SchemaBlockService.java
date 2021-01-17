package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaBlockRepository;

import java.util.List;
import java.util.Objects;
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

  public Optional<SchemaBlockDocument> getSchemaByNameAndVersion(String schemaName, String version) {
    Objects.requireNonNull(schemaName, "schema name cannot be null");
    Objects.requireNonNull(version, "schema version cannot be null"); //todo if version null get latest
    Optional<SchemaBlock> optionalSchema = schemaBlockRepository.findFirstBySchemaNameAndVersionOrderByVersionDesc(schemaName, version);
    return optionalSchema.map(s -> modelMapper.map(s, SchemaBlockDocument.class));
  }

  public SchemaBlockDocument createSchemaBlock(@NonNull SchemaBlockDocument schemaBlockDocument) {
    SchemaBlock schemaBlock = modelMapper.map(schemaBlockDocument, SchemaBlock.class);

    // Get Previous latest version if it exists
    Optional<SchemaBlock> optionalSchemaBlock = schemaBlockRepository
            .findFirstBySchemaNameOrderByVersionDesc(Objects.requireNonNull(schemaBlock.getSchemaName(), "schemaName cannot be null"));

    schemaBlock.setLatest(true);
    SchemaBlock resultSchemaBlock = schemaBlockRepository.insert(schemaBlock);
    updatePreviousLatest(optionalSchemaBlock, false);
    return modelMapper.map(resultSchemaBlock, SchemaBlockDocument.class);
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
    Optional<SchemaBlock> schemaBlockOptional = schemaBlockRepository.findById(schemaBlockDocument.getId());
    schemaBlockRepository.delete(schemaBlock);
    // Get Previous latest version if it exists
    if (schemaBlockOptional.isPresent() && schemaBlockOptional.get().isLatest()) {
      Optional<SchemaBlock> optionalSchemaBlock =
              schemaBlockRepository.findFirstBySchemaNameOrderByVersionDesc(
                      Objects.requireNonNull(
                              schemaBlockDocument.getId().substring(0,schemaBlockDocument.getId().lastIndexOf('/')), "schemaName cannot be null"));
      updatePreviousLatest(optionalSchemaBlock, true);
    }
  }

  public void deleteSchemaBlocksById(@NonNull String id) {
    Optional<SchemaBlock> schemaBlock = schemaBlockRepository.findById(id);
    schemaBlockRepository.deleteById(id);
    // Get Previous latest version if it exists
    if (schemaBlock.isPresent() && schemaBlock.get().isLatest()) {
      Optional<SchemaBlock> optionalSchemaBlock =
              schemaBlockRepository.findFirstBySchemaNameOrderByVersionDesc(
                      Objects.requireNonNull(
                              id.substring(0, id.lastIndexOf('/')), "schemaName cannot be null"));
      updatePreviousLatest(optionalSchemaBlock, true);
    }
  }

  public SchemaBlockDocument updateSchemaBlocks(SchemaBlockDocument schemaBlockDocument)
      throws JsonSchemaServiceException {
    if (schemaBlockRepository.existsById(schemaBlockDocument.getId())) {
      SchemaBlock schemaBlock = modelMapper.map(schemaBlockDocument, SchemaBlock.class);
      return modelMapper.map(schemaBlockRepository.save(schemaBlock), SchemaBlockDocument.class);
    } else {
      String errorMessage = "Provided schemaBlockDocument does not exists!";
      log.error(errorMessage);
      throw new JsonSchemaServiceException(errorMessage);
    }
  }

  private void updatePreviousLatest(Optional<SchemaBlock> optionalSchemaBlock, boolean latestStatus) {
    // change the latest status of previous if it exists
    if (optionalSchemaBlock.isPresent()) {
      SchemaBlock previousSchemaBlock = optionalSchemaBlock.get();
      previousSchemaBlock.setLatest(latestStatus);
      schemaBlockRepository.save(previousSchemaBlock);
    }
  }
}
