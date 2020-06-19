package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SchemaBlockServiceTest {

  @Mock
  private SchemaBlockRepository schemaBlockRepository;
  @Mock
  private ModelMapper modelMapper;
  private SchemaBlockService schemaBlockService;

  @BeforeEach
  public void init() {
    schemaBlockRepository = Mockito.mock(SchemaBlockRepository.class);
    modelMapper = Mockito.mock(ModelMapper.class);
    schemaBlockService = new SchemaBlockService(schemaBlockRepository, modelMapper);
    SchemaBlock schemaBlock = new SchemaBlock();
    when(schemaBlockRepository.save(any())).thenReturn(schemaBlock);
    when(schemaBlockRepository.findAll()).thenReturn(Collections.singletonList(schemaBlock));
  }

  @Test
  public void testCreateSchemaBlock() {
    SchemaBlockDocument schemaBlockDocument = new SchemaBlockDocument();
    SchemaBlock result  = schemaBlockService.createSchemaBlock(schemaBlockDocument);
    Assertions.assertNotNull(result, "result cannot be null");
  }

  @Test
  public void testCreateSchemaBlockNull() {
    Assertions.assertThrows(
        NullPointerException.class,
        () -> schemaBlockService.createSchemaBlock(null),
        "method argument cannot be null");
  }

  @Test
  public void testGetAllSchemaBlocks() {
    List<SchemaBlock> schemaBlocks = schemaBlockService.getAllSchemaBlocks();
    Assertions.assertFalse(schemaBlocks.isEmpty(), "schemaBlocks cannot be empty");
  }

  @Test
  public void testDeleteSchemaBlocksByIdNull() {
    Assertions.assertThrows(
        NullPointerException.class,
        () -> schemaBlockService.deleteSchemaBlocksById(null),
        "method argument cannot be null");
  }
}
