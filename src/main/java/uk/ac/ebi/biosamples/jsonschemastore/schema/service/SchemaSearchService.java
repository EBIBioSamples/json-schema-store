package uk.ac.ebi.biosamples.jsonschemastore.schema.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchemaSearchService {
  private final SchemaBlockRepository schemaBlockRepository;
  private final ModelMapper modelMapper;

  public SchemaSearchService(SchemaBlockRepository schemaBlockRepository, ModelMapper modelMapper) {
    this.schemaBlockRepository = schemaBlockRepository;
    this.modelMapper = modelMapper;
    this.modelMapper
        .getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
  }

  public Page<SchemaBlockDocument> getAllSchemaBlocksPage(Integer page, Integer size) {
    // only retrieve the latest version of schemas
    Page<SchemaBlock> schemaBlocks =
        schemaBlockRepository.findAllByLatestTrue(PageRequest.of(page, size));
    List<SchemaBlockDocument> schemaBlockDocuments =
        schemaBlocks.stream()
            .map(s -> modelMapper.map(s, SchemaBlockDocument.class))
            .collect(Collectors.toList());
    return new PageImpl<>(
        schemaBlockDocuments, PageRequest.of(page, size), schemaBlocks.getTotalElements());
  }

  public Page<SchemaBlockDocument> fullTextSearchSchemas(String searchKey, Integer page, Integer size) {
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

  public Page<SchemaBlockDocument> getListOfVersionsOfSchema(String schemaName, Integer page, Integer size) {
    Page<SchemaBlock> schemaBlocks =
            schemaBlockRepository.findBySchemaNameOrderByVersionDesc(schemaName, PageRequest.of(page, size));
    List<SchemaBlockDocument> schemaBlockDocuments =
            schemaBlocks.stream()
                    .map(schemaBlock -> modelMapper.map(schemaBlock, SchemaBlockDocument.class))
                    .collect(Collectors.toList());
    return new PageImpl<>(
            schemaBlockDocuments, PageRequest.of(page, size), schemaBlocks.getTotalElements());
  }
}
