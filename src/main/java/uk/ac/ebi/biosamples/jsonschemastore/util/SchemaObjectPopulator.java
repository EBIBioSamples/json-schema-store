package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.databind.JsonNode;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.Schema;

import java.util.Objects;

public class SchemaObjectPopulator {

    public static void populateSchemaRequestFields(Schema schema) {
        JsonNode node = schema.getSchema();
        String schemaId = Objects.requireNonNull(node.get("$id"), "$id field cannot be null!").asText();
        schema.setId(schemaId);
        schema.setName(SchemaIdExtractor.getSchemaName(schemaId));
        schema.setVersion(SchemaIdExtractor.getVersion(schemaId));
        schema.setTitle(node.get("title") != null ? node.get("title").asText() : "");
        schema.setDescription(node.get("description") != null ? node.get("description").asText() : "");
    }
}
