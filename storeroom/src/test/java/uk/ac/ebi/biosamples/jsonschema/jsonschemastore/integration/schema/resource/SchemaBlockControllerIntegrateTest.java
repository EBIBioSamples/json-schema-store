package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
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
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.util.AppClientHelper;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.util.SchemaBlockFactoryUtil;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class SchemaBlockControllerIntegrateTest {

  private static final String jwt = AppClientHelper.getToken();

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

  @BeforeAll
  public static void setup() {
    environment.start();
  }

  @BeforeEach
  public void init() throws JsonProcessingException {
    schemaBlockRepository.deleteAll();
    schemaBlock = SchemaBlockFactoryUtil.getSchemaBlockObject();
  }

  @Test
  public void testCreateSchemaBlock() throws Exception {
      assertEquals(0L, schemaBlockRepository.count());
      RequestBuilder requestBuilder =
          MockMvcRequestBuilders.post("/api/v1/schemas")
              .header(AppClientHelper.AUTHORIZATION, jwt)
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
  }

  @Test
  public void testUpdateSchemaBlocks() throws Exception {
      schemaBlockRepository.save(schemaBlock);
      assertEquals(1, schemaBlockRepository.count());
      SchemaBlockDocument schemaBlockDocument =
          objectMapper.readValue(SchemaBlockFactoryUtil.SCHEMA, SchemaBlockDocument.class);
      ObjectNode objectNode =
          (ObjectNode) objectMapper.readTree(schemaBlockDocument.getJsonSchema());
      String newTitle = "Disease new";
      objectNode.put("title", newTitle);
      RequestBuilder requestBuilder =
          MockMvcRequestBuilders.put("/api/v1/schemas")
              .header(AppClientHelper.AUTHORIZATION, jwt)
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
  }
}
