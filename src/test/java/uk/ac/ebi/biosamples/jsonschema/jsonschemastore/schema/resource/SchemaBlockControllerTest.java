package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.ValidatorClient;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service.SchemaBlockService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SchemaBlockControllerTest {

  @MockBean private SchemaBlockService schemaBlockService;
  @MockBean private ValidatorClient validatorClient;
  @MockBean private Environment environment;
  private SchemaBlockController schemaBlockController;
  private ObjectMapper objectMapper;

  @BeforeEach
  public void init() {
    schemaBlockService = mock(SchemaBlockService.class);
    validatorClient = mock(ValidatorClient.class);
    environment = mock(Environment.class);
    schemaBlockController =
        new SchemaBlockController(schemaBlockService, validatorClient, environment);
    objectMapper = new ObjectMapper();
  }

  @Test
  public void testGetAllSchemaBlock() {
    SchemaBlockDocument schemaBlockDocument = new SchemaBlockDocument();
    schemaBlockDocument.setTitle("test object");

    when(schemaBlockService.getAllSchemaBlocks())
        .thenReturn(Collections.singletonList(schemaBlockDocument));
    ResponseEntity<List<SchemaBlockDocument>> responseEntity =
        schemaBlockController.getAllSchemaBlocks();
    Assertions.assertNotNull(responseEntity, "responseEntity cannot be null.");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(responseEntity.getBody(), "responseEntity body cannot be null.");
    assertEquals(schemaBlockDocument, responseEntity.getBody().get(0));
  }

  @Test
  public void testDeleteSchemaBlocksById() {
    ResponseEntity<String> responseEntity = schemaBlockController.deleteSchemaBlocksById("1234");
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
  }
}
