package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.model.Schema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;
import uk.ac.ebi.biosamples.jsonschemastore.service.ChecklistGroupService;

@Component
@RequiredArgsConstructor
public class SchemaObjectPopulator {
    private final SchemaStoreProperties properties;
    private final ChecklistGroupService checklistGroupService;


    public void populateSchema(Schema schema) {
        SchemaId schemaId = toSchemaId(schema);
        populateWithSchemaId(schema, schemaId);
        populateAuthority(schema);
        addSchemaUniqueId(schema, schemaId);
        schema.setGroup(checklistGroupService.findGroupForAccession(schemaId.getAccession()));
    }

    public  void addSchemaUniqueId(Schema schema, SchemaId schemaId) {
        String schemaRestResource = getSchemaResourceURL(schemaId);
        ((ObjectNode) schema.getSchema()).put("$id", schemaRestResource);
    }

    public String getSchemaResourceURL(SchemaId schemaId) {
        return getSchemasRegistryResourceRoot() + "?id=" + schemaId.asString();
    }

    public void incrementAndPopulateSchema(Schema schema) {
        SchemaId schemaId = toSchemaId(schema);
        SchemaId incrementedId = SchemaId.incrementMinorVersion(schemaId);
        populateWithSchemaId(schema, incrementedId);
        addSchemaUniqueId(schema, incrementedId);
        populateAuthority(schema);
    }

    private  String getSchemasRegistryResourceRoot() {
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .replacePath("/registry/schemas")
                .build()
                .toUriString();
    }

    private static SchemaId toSchemaId(Schema schema) {
        // TODO: check if schema's $id is populated, get version from there
        String accession = schema.getAccession();
        String version = schema.getVersion();
        return new SchemaId(accession, version);
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
