package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Get Previous latest version if it exists
    Optional<SchemaBlock> optionalSchemaBlock = schemaBlockRepository
            .findFirstBySchemaNameOrderByVersionDesc(Objects.requireNonNull(schemaBlock.getSchemaName(), "schemaName cannot be null"));

    schemaBlock.setLatest(true);
    SchemaBlock resultSchemaBlock = schemaBlockRepository.insert(schemaBlock);
    updatePreviousLatest(optionalSchemaBlock);
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

  public Page<SchemaBlockDocument> getAllSchemaBlocksPage(Integer page, Integer size) {
    Page<SchemaBlock> schemaBlocks = schemaBlockRepository.findAll(PageRequest.of(page, size));
    List<SchemaBlockDocument> schemaBlockDocuments =
        schemaBlocks.stream()
            .map(s -> modelMapper.map(s, SchemaBlockDocument.class))
            .collect(Collectors.toList());
    return new PageImpl<>(
        schemaBlockDocuments, PageRequest.of(page, size), schemaBlocks.getTotalElements());
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

  public Page<SchemaBlockDocument> searchSchemas(String searchKey, Integer page, Integer size) {
    TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(searchKey);
    Page<SchemaBlock> schemaBlocks =
        schemaBlockRepository.findAllBy(textCriteria, PageRequest.of(page, size));
    List<SchemaBlockDocument> schemaBlockDocuments =
        schemaBlocks.stream()
            .map(schemaBlock -> modelMapper.map(schemaBlock, SchemaBlockDocument.class))
            .collect(Collectors.toList());
    return new PageImpl<>(
        schemaBlockDocuments, PageRequest.of(page, size), schemaBlocks.getTotalElements());
  }

  private void updatePreviousLatest(Optional<SchemaBlock> optionalSchemaBlock) {
    // change the latest status of previous if it exists
    if (optionalSchemaBlock.isPresent()) {
      SchemaBlock previousSchemaBlock = optionalSchemaBlock.get();
      previousSchemaBlock.setLatest(false);
      schemaBlockRepository.save(previousSchemaBlock);
    }
  }
}
