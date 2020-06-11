package uk.ac.ebi.biosamples.jsonschemastore.schema.resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class SchemaBlockControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testGetAllSchemaBlock() throws Exception {
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/ping");
    MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
    Assertions.assertEquals("it's working!", mvcResult.getResponse().getContentAsString());
  }
}
