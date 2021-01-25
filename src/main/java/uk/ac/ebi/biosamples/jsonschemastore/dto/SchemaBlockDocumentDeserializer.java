package uk.ac.ebi.biosamples.jsonschemastore.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SchemaBlockDocumentDeserializer extends StdDeserializer<SchemaBlockDocument> {
  private static ObjectMapper objectMapper =  new ObjectMapper();
  public SchemaBlockDocumentDeserializer() {
    this(null);
  }

  protected SchemaBlockDocumentDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public SchemaBlockDocument deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
    return SchemaBlockDocument.builder()
        .id(Objects.requireNonNull(jsonNode.get("$id"), "$id filed cannot be null!").asText())
        .version(getVersion(jsonNode))
        .schemaName(getSchemaName(jsonNode))
        .schema(getString(jsonNode.get("$schema")))
        .description(getString(jsonNode.get("description")))
        .type(getString(jsonNode.get("type")))
        .title(getString(jsonNode.get("title")))
        .additionalProperties(getBoolean(jsonNode.get("additionalProperties")))
        .required(getRequired(jsonNode.get("required")))
        .meta(getMeta(jsonNode.get("meta")))
        .jsonSchema(jsonNode.toString())
        .build();
  }

  private static Map<String, Object> getMeta(JsonNode jsonNode) throws JsonProcessingException {
    if (Objects.nonNull(jsonNode)) {
      return objectMapper.readValue(jsonNode.toString(), new TypeReference<>() {});
    } else {
      return null;
    }
  }

  @Nullable
  private static List<String> getRequired(JsonNode jsonNode) {
    if (Objects.nonNull(jsonNode)) {
      return StreamSupport.stream(jsonNode.spliterator(), false)
          .map(JsonNode::asText)
          .collect(Collectors.toList());
    } else {
      return null;
    }
  }

  @Nullable
  private static Boolean getBoolean(JsonNode jsonNode) {
    if (Objects.nonNull(jsonNode)) {
      return jsonNode.booleanValue() ? Boolean.TRUE : Boolean.FALSE;
    } else {
      return null;
    }
  }

  @Nullable
  private static String getString(JsonNode jsonNode) {
    if (Objects.nonNull(jsonNode)) {
      return jsonNode.asText();
    } else {
      return null;
    }
  }

  private String getVersion(JsonNode jsonNode) {
    String schemaId = Objects.requireNonNull(jsonNode.get("$id"), "$id filed cannot be null!").asText();
    String[] pathSegments = decomposeSchemaId(schemaId);
    String schemaVersion = "1.0.0";
    if (pathSegments.length > 1) {
      schemaVersion = pathSegments[pathSegments.length - 2];
    }

    return schemaVersion;
  }

  private String getSchemaName(JsonNode jsonNode) {
    String schemaId = Objects.requireNonNull(jsonNode.get("$id"), "$id filed cannot be null!").asText();
    String[] pathSegments = decomposeSchemaId(schemaId);
    String schemaName;
    if (pathSegments.length > 1) {
      schemaName = pathSegments[pathSegments.length - 1];
    } else {
      schemaName = pathSegments[0];
    }

    return schemaName;
  }

  private String[] decomposeSchemaId(String schemaId) {
    String path;
    try {
      path = new URL(schemaId).getPath();
    } catch (MalformedURLException e) {
      path = schemaId;
    }
    return path.split("/");
  }
}
