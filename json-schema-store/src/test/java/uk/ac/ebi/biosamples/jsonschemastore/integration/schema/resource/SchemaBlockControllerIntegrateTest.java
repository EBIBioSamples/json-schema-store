package uk.ac.ebi.biosamples.jsonschemastore.integration.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.ac.ebi.biosamples.jsonschemastore.integration.util.SchemaBlockFactoryUtil;
import uk.ac.ebi.biosamples.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.schema.repository.SchemaBlockRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class SchemaBlockControllerIntegrateTest {

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
    // TODO: Elixir json schema validator docker container should be running
    assertEquals(0L, schemaBlockRepository.count());
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/api/v1/schemas")
            .contentType("application/json")
            .content(SchemaBlockFactoryUtil.SCHEMA);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(201, mvcResult.getResponse().getStatus(), "Response status was not 201.");
    assertEquals(1L, schemaBlockRepository.count());
    SchemaBlock resultSchemaBlock = schemaBlockRepository.findAll().get(0);
    schemaBlock.setId(resultSchemaBlock.getId()); // TODO: we we insert schema block should have an id
    assertEquals(schemaBlock, resultSchemaBlock, "saved schemaBlock  is not equal.");
  }
}
