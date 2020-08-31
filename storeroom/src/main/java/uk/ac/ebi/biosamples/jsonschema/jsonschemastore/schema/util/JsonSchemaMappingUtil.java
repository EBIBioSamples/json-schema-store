package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonSchemaMappingUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String $_REF = "$ref";
  private static final String _REF = "_ref";

  public static <T> JsonNode convertSchemaBlockToJson(T schema) throws JsonProcessingException {
    if (String.class.getName().equals(schema.getClass().getName())) {
      String json = (String) schema;
      return objectMapper.readTree(json);
    }
    ObjectNode jsonNode = objectMapper.valueToTree(schema);
    jsonNode.remove("schemalessData");
    return jsonNode;
  }

  public static <T> JsonNode convertObjectToJson(T object) {
    return objectMapper.valueToTree(object);
  }

  public static JsonNode getSchemaObject() {
    try {
      return objectMapper.readTree(JsonSchemaMappingUtil.class.getResourceAsStream("/test/metaschema.json"));
    } catch (JsonProcessingException e) {
      log.error("Error! ", e);
      return null;
    } catch (IOException e) {
      log.error("Meta Schema is not found in the given location", e);
      return null;
    }
  }


}
