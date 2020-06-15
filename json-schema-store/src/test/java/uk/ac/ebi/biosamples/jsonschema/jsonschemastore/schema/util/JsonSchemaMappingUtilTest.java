package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document.SchemaBlock;

import java.util.Collections;

class JsonSchemaMappingUtilTest {

  @Test
  public void testConvertSchemaBlockToJson() {
    SchemaBlock schemaBlock = new SchemaBlock();
    schemaBlock.setDescription("test description");
    schemaBlock.setTitle("schema title");
    schemaBlock.setSchemalessData(
        Collections.singletonMap("example", "{\"test1\": \"test1\", \"test2\": \"test2\"}"));
    JsonNode jsonNode = JsonSchemaMappingUtil.convertSchemaBlockToJson(schemaBlock);

    Assertions.assertNotNull(jsonNode, "jsonNode cannot be null.");
    Assertions.assertFalse(jsonNode.has("schemalessData"), "schemalessData cannot exist.");
    Assertions.assertTrue(jsonNode.has("description"));
    Assertions.assertEquals(
        "test description",
        jsonNode.get("description").asText(),
        "description value did not match");
    Assertions.assertTrue(jsonNode.has("title"));
    Assertions.assertEquals(
        "schema title", jsonNode.get("title").asText(), "title value did not match.");
  }

  @Test
  public void testConvertObjectToJson() {
    SchemaBlockDocument schemaBlockDocument = new SchemaBlockDocument();
    schemaBlockDocument.setDescription("test description");
    schemaBlockDocument.setTitle("schema title");
    JsonNode jsonNode = JsonSchemaMappingUtil.convertObjectToJson(schemaBlockDocument);

    Assertions.assertNotNull(jsonNode, "jsonNode cannot be null.");
    Assertions.assertTrue(jsonNode.has("description"));
    Assertions.assertEquals(
            "test description",
            jsonNode.get("description").asText(),
            "description value did not match");
    Assertions.assertTrue(jsonNode.has("title"));
    Assertions.assertEquals(
            "schema title", jsonNode.get("title").asText(), "title value did not match.");
  }

}
