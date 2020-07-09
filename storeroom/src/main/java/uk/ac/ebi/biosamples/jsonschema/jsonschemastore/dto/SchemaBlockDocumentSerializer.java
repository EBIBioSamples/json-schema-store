package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SchemaBlockDocumentSerializer extends StdSerializer<SchemaBlockDocument> {

  public SchemaBlockDocumentSerializer() {
    this(null);
  }

  public SchemaBlockDocumentSerializer(Class<SchemaBlockDocument> t) {
    super(t);
  }

  @Override
  public void serialize(
      SchemaBlockDocument schemaBlockDocument,
      JsonGenerator jsonGenerator,
      SerializerProvider provider)
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(schemaBlockDocument.getJsonSchema());
    jsonGenerator.writeTree(jsonNode);
  }
}
