package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.model.Schema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;

@Component
public class SchemaObjectPopulator {
    private final SchemaStoreProperties properties;

    public SchemaObjectPopulator(SchemaStoreProperties properties) {
        this.properties = properties;
    }

    public void populateSchema(Schema schema) {
        SchemaId schemaId = toSchemaId(schema);
        populateWithSchemaId(schema, schemaId);
        populateAuthority(schema);
    }

    public void incrementAndPopulateSchema(Schema schema) {
        SchemaId schemaId = toSchemaId(schema);
        SchemaId incrementedId = SchemaId.incrementMinorVersion(schemaId);
        populateWithSchemaId(schema, incrementedId);
        ((ObjectNode)schema.getSchema()).put("$id", "base-url-not-set/"+incrementedId.asString());
        populateAuthority(schema);
    }

    private static SchemaId toSchemaId(Schema schema) {
        // TODO: check if schema's $id is populated, get version from there
        JsonNode node = schema.getSchema();
        String accession = schema.getAccession();
        String version = schema.getVersion();
        SchemaId schemaId = new SchemaId(accession, version);
        return schemaId;
    }

    private void populateAuthority(Schema schema) {
        String authority = schema.getAuthority();
        if (authority == null || authority.isEmpty()) {
            schema.setAuthority(properties.getDefaultAuthority());
        }
    }

    private void populateWithSchemaId(Schema schema, SchemaId schemaId) {
        JsonNode node = schema.getSchema();
        schema.setId(schemaId.asString());
        schema.setVersion(schemaId.getVersion());
        schema.setTitle(node.get("title") != null ? node.get("title").asText() : "");
        schema.setName(schema.getTitle());
        schema.setDescription(node.get("description") != null ? node.get("description").asText() : "");
    }
}
