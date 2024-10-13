package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.model.Schema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

@Component
@RequiredArgsConstructor
public class SchemaObjectPopulator {
    private final SchemaStoreProperties properties;
    private final RestResourcePathProvider restResourcePathProvider;

    public void populateSchema(Schema schema) {
        SchemaId schemaId = toSchemaId(schema);
        populateWithSchemaId(schema, schemaId);
        populateAuthority(schema);
        addSchemaUniqueId(schema, schemaId);
    }

    public  void addSchemaUniqueId(Schema schema, SchemaId schemaId) {
        String schemaRestResource = getSchemaResuorceURL(schemaId);
        ((ObjectNode) schema.getSchema()).put("$id", schemaRestResource);
    }

    public String getSchemaResuorceURL(SchemaId schemaId) {
        String schemaRestResource = getSchemasResourceRoot() + "/" + schemaId.asString();
        return schemaRestResource;
    }

    public void incrementAndPopulateSchema(Schema schema) {
        SchemaId schemaId = toSchemaId(schema);
        SchemaId incrementedId = SchemaId.incrementMinorVersion(schemaId);
        populateWithSchemaId(schema, incrementedId);
        addSchemaUniqueId(schema, incrementedId);
        populateAuthority(schema);
    }

    private  String getSchemasResourceRoot() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath(restResourcePathProvider.getResourcePath(MongoJsonSchema.class))
                .build()
                .toUriString();
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
