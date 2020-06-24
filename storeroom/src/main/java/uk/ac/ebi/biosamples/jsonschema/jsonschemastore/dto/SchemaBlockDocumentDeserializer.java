package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SchemaBlockDocumentDeserializer extends StdDeserializer<SchemaBlockDocument> {
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
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.readValue(
        jsonNode.required("meta").toString(), new TypeReference<Map<String, Object>>() {});
    return SchemaBlockDocument.builder()
        .id(jsonNode.get("$id").toString())
        .schema(jsonNode.get("$schema").toString())
        .title(jsonNode.get("title").toString())
        .description(jsonNode.get("description").toString())
        .type(jsonNode.get("type").toString())
        .additionalProperties(jsonNode.get("additionalProperties").booleanValue())
        .required(
            Objects.requireNonNullElse(
                StreamSupport.stream(jsonNode.required("required").spliterator(), false)
                    .map(JsonNode::toString)
                    .collect(Collectors.toList()),
                new ArrayList<>()))
        .meta(
            objectMapper.readValue(
                jsonNode.required("meta").toString(), new TypeReference<Map<String, Object>>() {}))
        .jsonSchema(jsonNode.toPrettyString())
        .build();
  }
}
