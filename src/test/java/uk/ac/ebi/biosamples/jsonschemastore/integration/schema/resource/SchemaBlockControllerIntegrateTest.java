package uk.ac.ebi.biosamples.jsonschemastore.integration.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
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
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.integration.util.SchemaBlockFactoryUtil;
import uk.ac.ebi.biosamples.jsonschemastore.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaBlockRepository;

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
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/schemas");
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(200, mvcResult.getResponse().getStatus(), "status code is not equal.");
    Assertions.assertEquals(
        modelMapper.map(schemaBlock, SchemaBlockDocument.class),
        objectMapper
            .readValue(mvcResult.getResponse().getContentAsString(), SchemaBlockDocument[].class)[0],
        "schemaBlock is not equal.");
  }

  @Test
  public void testGetSchemaBlockById() throws Exception {
    schemaBlockRepository.save(schemaBlock);
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get("/api/v1/schemas/").param("id", schemaBlock.getId());
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
    RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/schemas")
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
    RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/v1/schemas/").param("id", schemaBlock.getId());
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(204, mvcResult.getResponse().getStatus(), "status code is not equal.");
    assertEquals(0, schemaBlockRepository.count(), "count should be 0 after deleting");
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
      assertEquals(schemaBlock.getId(), resultSchemaBlock.getId());
      JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
      assertEquals(
          modelMapper.map(schemaBlock, SchemaBlockDocument.class),
          objectMapper.readValue(jsonNode.toPrettyString(), SchemaBlockDocument.class),
          "schemaBlockDocument ids are not equal.");
    } finally {
      environment.stop();
    }
  }

  @Test
  public void testUpdateSchemaBlocks() throws Exception {
    try {
      schemaBlockRepository.save(schemaBlock);
      assertEquals(1, schemaBlockRepository.count());
      environment.start();
      SchemaBlockDocument schemaBlockDocument = objectMapper.readValue(SchemaBlockFactoryUtil.SCHEMA, SchemaBlockDocument.class);
      ObjectNode objectNode = (ObjectNode) objectMapper.readTree(schemaBlockDocument.getJsonSchema());
      String newTitle = "Disease new";
      objectNode.put("title", newTitle);
      RequestBuilder requestBuilder =
              MockMvcRequestBuilders.put("/api/v1/schemas")
                      .contentType("application/json")
                      .content(objectNode.toPrettyString());
      MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
      assertEquals(201, mvcResult.getResponse().getStatus(), "Response status was not 201.");
      assertEquals(1L, schemaBlockRepository.count());
      SchemaBlock resultSchemaBlock = schemaBlockRepository.findAll().get(0);
      assertEquals(schemaBlock.getId(), resultSchemaBlock.getId());
      JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
      assertEquals(schemaBlock.getId(), jsonNode.get("$id").asText());
      assertEquals(newTitle, jsonNode.get("title").asText());
    } finally {
      environment.stop();
    }
  }
}
