package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SchemaBlockServiceTest {

  @Mock
  private SchemaBlockRepository schemaBlockRepository;
  private SchemaBlockService schemaBlockService;

  @BeforeEach
  public void init() {
    schemaBlockRepository = Mockito.mock(SchemaBlockRepository.class);
    schemaBlockService = new SchemaBlockService(schemaBlockRepository);
    SchemaBlock schemaBlock = new SchemaBlock();
    when(schemaBlockRepository.save(any())).thenReturn(schemaBlock);
    when(schemaBlockRepository.findAll()).thenReturn(Collections.singletonList(schemaBlock));
  }

  @Test
  public void testCreateSchemaBlock() {
    SchemaBlock schemaBlock = new SchemaBlock();
    SchemaBlock result  = schemaBlockService.createSchemaBlock(schemaBlock);
    Assertions.assertNotNull(result, "result cannot be null");
  }

  @Test
  public void testGetAllSchemaBlocks() {
    List<SchemaBlock> schemaBlocks = schemaBlockService.getAllSchemaBlocks();
    Assertions.assertFalse(schemaBlocks.isEmpty(), "schemaBlocks cannot be empty");
  }
}
