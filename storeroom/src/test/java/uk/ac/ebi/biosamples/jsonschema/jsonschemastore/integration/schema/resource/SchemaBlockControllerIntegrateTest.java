package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.util.SchemaBlockFactoryUtil;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class SchemaBlockControllerIntegrateTest {

  @ClassRule
  private static final DockerComposeContainer environment =
      new DockerComposeContainer(
              new File(
                  "src/test/java/uk/ac/ebi/biosamples/jsonschema/jsonschemastore/integration/resources/compose-test.yml"))
          .withLocalCompose(false)
          .waitingFor("validator_1", Wait.forHttp("/validate"));

  @Autowired private MockMvc mockMvc;
  @Autowired private SchemaBlockRepository schemaBlockRepository;
  @Autowired private ObjectMapper objectMapper;
  private SchemaBlock schemaBlock;

  @BeforeEach
  public void init() throws JsonProcessingException {
    schemaBlockRepository.deleteAll();
    schemaBlock = SchemaBlockFactoryUtil.getSchemaBlockObject();
  }

  @Test
  public void testGetAllSchemaBlock() throws Exception {
    schemaBlockRepository.save(schemaBlock);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/schemas");
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus(), "status code is not equal.");
    assertEquals(
        schemaBlock,
        objectMapper
            .readValue(mvcResult.getResponse().getContentAsString(), SchemaBlock[].class)[0],
        "schemaBlock is not equal.");
  }

  @Test
  public void testCreateSchemaBlock() throws Exception {
    try {
      environment.start();
      assertEquals(0L, schemaBlockRepository.count());
      RequestBuilder requestBuilder =
          MockMvcRequestBuilders.post("/api/v1/schemas")
              .contentType("application/json")
              .content(SchemaBlockFactoryUtil.SCHEMA);
      MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
      assertEquals(201, mvcResult.getResponse().getStatus(), "Response status was not 201.");
      assertEquals(1L, schemaBlockRepository.count());
      SchemaBlock resultSchemaBlock = schemaBlockRepository.findAll().get(0);
//      schemaBlock.setId(
//          resultSchemaBlock.getId()); // TODO: we we insert schema block should have an id
//      assertEquals(schemaBlock, resultSchemaBlock, "saved schemaBlock  is not equal.");
    } finally {
      environment.stop();
    }
  }
}
