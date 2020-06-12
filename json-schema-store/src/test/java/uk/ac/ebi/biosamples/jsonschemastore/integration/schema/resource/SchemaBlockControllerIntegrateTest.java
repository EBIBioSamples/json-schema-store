package uk.ac.ebi.biosamples.jsonschemastore.integration.schema.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class SchemaBlockControllerIntegrateTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private SchemaBlockRepository schemaBlockRepository;
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
    Assertions.assertEquals(200, mvcResult.getResponse().getStatus(), "status code is not equal");
  }
}
