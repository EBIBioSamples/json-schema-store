package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.util.AppClientHelper;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.util.SchemaBlockFactoryUtil;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class SchemaBlockControllerIntegrateFastTest {

  private static final String jwt = AppClientHelper.getToken();

  @Autowired private MockMvc mockMvc;
  @Autowired private SchemaBlockRepository schemaBlockRepository;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ModelMapper modelMapper;
  private SchemaBlock schemaBlock;

  @BeforeEach
  public void init() throws JsonProcessingException {
    schemaBlockRepository.deleteAll();
    schemaBlock = SchemaBlockFactoryUtil.getSchemaBlockObject();
  }

  @Test
  public void testGetAllSchemaBlock() throws Exception {
    schemaBlockRepository.save(schemaBlock);
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/api/v1/schemas").header(AppClientHelper.AUTHORIZATION, jwt);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus(), "status code is not equal.");
    assertEquals(
        modelMapper.map(schemaBlock, SchemaBlockDocument.class),
        objectMapper
            .readValue(mvcResult.getResponse().getContentAsString(), SchemaBlockDocument[].class)[
            0],
        "schemaBlock is not equal.");
  }

  @Test
  public void testGetSchemaBlockById() throws Exception {
    schemaBlockRepository.save(schemaBlock);
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/api/v1/schemas/")
            .param("id", schemaBlock.getId())
            .header(AppClientHelper.AUTHORIZATION, jwt);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus(), "status code is not equal.");
    JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
    assertEquals(
        modelMapper.map(schemaBlock, SchemaBlockDocument.class),
        objectMapper.readValue(jsonNode.toPrettyString(), SchemaBlockDocument.class),
        "schemaBlockDocument ids are not equal.");
  }

  @Test
  public void testDeleteSchemaBlocks() throws Exception {
    schemaBlockRepository.save(schemaBlock);
    assertEquals(1, schemaBlockRepository.count());
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.delete("/api/v1/schemas")
            .header(AppClientHelper.AUTHORIZATION, jwt)
            .contentType("application/json")
            .content(SchemaBlockFactoryUtil.SCHEMA);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(204, mvcResult.getResponse().getStatus(), "status code is not equal.");
    assertEquals(0, schemaBlockRepository.count(), "count should be 0 after deleting");
  }

  @Test
  public void testDeleteSchemaBlocksById() throws Exception {
    schemaBlockRepository.save(schemaBlock);
    assertEquals(1, schemaBlockRepository.count());
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.delete("/api/v1/schemas/")
            .param("id", schemaBlock.getId())
            .header(AppClientHelper.AUTHORIZATION, jwt);
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(204, mvcResult.getResponse().getStatus(), "status code is not equal.");
    assertEquals(0, schemaBlockRepository.count(), "count should be 0 after deleting");
  }
}
