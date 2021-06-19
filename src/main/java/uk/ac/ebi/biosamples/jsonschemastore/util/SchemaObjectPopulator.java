package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import uk.ac.ebi.biosamples.jsonschemastore.model.Schema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;

import java.util.Objects;

public class SchemaObjectPopulator {

    public static void populateSchema(Schema schema) {
        JsonNode node = schema.getSchema();
        String id = Objects.requireNonNull(node.get("$id"), "$id field cannot be null!").asText();
        SchemaId schemaId = SchemaIdExtractor.validateSchemaId(id);
        populateWithSchemaId(schema, schemaId);
    }

    public static void incrementAndPopulateSchema(Schema schema) {
        JsonNode node = schema.getSchema();
        String id = Objects.requireNonNull(node.get("$id"), "$id field cannot be null!").asText();
        SchemaId schemaId = SchemaIdExtractor.incrementSchemaId(id);
        populateWithSchemaId(schema, schemaId);
        ((ObjectNode)schema.getSchema()).put("$id", schemaId.getId());
    }

    private static void populateWithSchemaId(Schema schema, SchemaId schemaId) {
        JsonNode node = schema.getSchema();
        schema.setId(schemaId.getId());
        schema.setName(schemaId.getName());
        schema.setVersion(schemaId.getVersion());
        schema.setDomain(schemaId.getDomain());
        schema.setTitle(node.get("title") != null ? node.get("title").asText() : "");
        schema.setDescription(node.get("description") != null ? node.get("description").asText() : "");
    }
}
