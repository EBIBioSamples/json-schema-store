package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.model.Schema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;

import java.util.Objects;

@Component
public class SchemaObjectPopulator {
    private final SchemaStoreProperties properties;

    public SchemaObjectPopulator(SchemaStoreProperties properties) {
        this.properties = properties;
    }

    public void populateSchema(Schema schema) {
        JsonNode node = schema.getSchema();
        String id = Objects.requireNonNull(node.get("$id"), "$id field cannot be null!").asText();
        SchemaId schemaId = SchemaIdExtractor.validateSchemaId(id);
        populateWithSchemaId(schema, schemaId);
        populateAuthority(schema);
    }

    public void incrementAndPopulateSchema(Schema schema) {
        JsonNode node = schema.getSchema();
        String id = Objects.requireNonNull(node.get("$id"), "$id field cannot be null!").asText();
        SchemaId schemaId = SchemaIdExtractor.incrementSchemaId(id);
        populateWithSchemaId(schema, schemaId);
        ((ObjectNode)schema.getSchema()).put("$id", schemaId.getId());
        populateAuthority(schema);
    }

    private void populateAuthority(Schema schema) {
        String authority = schema.getAuthority();
        if (authority == null || authority.isEmpty()) {
            schema.setAuthority(properties.getDefaultAuthority());
        }
    }

    private void populateWithSchemaId(Schema schema, SchemaId schemaId) {
        JsonNode node = schema.getSchema();
        schema.setId(schemaId.getId());
        schema.setName(schemaId.getName());
        schema.setVersion(schemaId.getVersion());
        schema.setDomain(schemaId.getDomain());
        schema.setTitle(node.get("title") != null ? node.get("title").asText() : "");
        schema.setDescription(node.get("description") != null ? node.get("description").asText() : "");
    }
}
