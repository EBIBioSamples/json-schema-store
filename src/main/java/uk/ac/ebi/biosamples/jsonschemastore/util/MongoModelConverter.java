package uk.ac.ebi.biosamples.jsonschemastore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoMetaSchema;

@Slf4j
@Service
public class MongoModelConverter {
    private ObjectMapper objectMapper;

    public MongoModelConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MongoJsonSchema jsonSchemaToMongoJsonSchema(JsonSchema jsonSchema) {
        MongoJsonSchema mongoJsonSchema = new MongoJsonSchema();
        mongoJsonSchema.setId(jsonSchema.getId());
        mongoJsonSchema.setName(jsonSchema.getName());
        mongoJsonSchema.setVersion(jsonSchema.getVersion());
        mongoJsonSchema.setTitle(jsonSchema.getTitle());
        mongoJsonSchema.setDescription(jsonSchema.getDescription());
        mongoJsonSchema.setDomain(jsonSchema.getDomain());
        mongoJsonSchema.setMetaSchema(jsonSchema.getMetaSchema());
        mongoJsonSchema.setSchema(jsonSchema.getSchema().toString());

        return mongoJsonSchema;
    }

    public JsonSchema mongoJsonSchemaToJsonSchema(MongoJsonSchema mongoJsonSchema) {
        JsonSchema jsonSchema = new JsonSchema();
        jsonSchema.setId(mongoJsonSchema.getId());
        jsonSchema.setName(mongoJsonSchema.getName());
        jsonSchema.setVersion(mongoJsonSchema.getVersion());
        jsonSchema.setTitle(mongoJsonSchema.getTitle());
        jsonSchema.setDescription(mongoJsonSchema.getDescription());
        jsonSchema.setDomain(mongoJsonSchema.getDomain());
        jsonSchema.setMetaSchema(mongoJsonSchema.getMetaSchema());
        try {
            jsonSchema.setSchema(objectMapper.readTree(mongoJsonSchema.getSchema()));
        } catch (JsonProcessingException e) {
            log.error("Failed to convert mongo schema to JSON", e);
        }

        return jsonSchema;
    }

    public MongoMetaSchema metaSchemaToMongoMetaSchema(MetaSchema jsonSchema) {
        MongoMetaSchema mongoJsonSchema = new MongoMetaSchema();
        mongoJsonSchema.setId(jsonSchema.getId());
        mongoJsonSchema.setName(jsonSchema.getName());
        mongoJsonSchema.setVersion(jsonSchema.getVersion());
        mongoJsonSchema.setTitle(jsonSchema.getTitle());
        mongoJsonSchema.setDescription(jsonSchema.getDescription());
        mongoJsonSchema.setDomain(jsonSchema.getDomain());
        mongoJsonSchema.setMetaSchema(jsonSchema.getMetaSchema());
        mongoJsonSchema.setSchema(jsonSchema.getSchema().toString());

        return mongoJsonSchema;
    }

    public MetaSchema mongoMetaSchemaToMetaSchema(MongoMetaSchema mongoJsonSchema) {
        MetaSchema jsonSchema = new MetaSchema();
        jsonSchema.setId(mongoJsonSchema.getId());
        jsonSchema.setName(mongoJsonSchema.getName());
        jsonSchema.setVersion(mongoJsonSchema.getVersion());
        jsonSchema.setTitle(mongoJsonSchema.getTitle());
        jsonSchema.setDescription(mongoJsonSchema.getDescription());
        jsonSchema.setDomain(mongoJsonSchema.getDomain());
        jsonSchema.setMetaSchema(mongoJsonSchema.getMetaSchema());
        try {
            jsonSchema.setSchema(objectMapper.readTree(mongoJsonSchema.getSchema()));
        } catch (JsonProcessingException e) {
            log.error("Failed to convert mongo schema to JSON", e);
        }

        return jsonSchema;
    }

    public SchemaOutline mongoJsonSchemaToSchemaOutline(MongoJsonSchema mongoJsonSchema) {
        SchemaOutline schema = new SchemaOutline();
        schema.setId(mongoJsonSchema.getId());
        schema.setName(mongoJsonSchema.getName());
        schema.setVersion(mongoJsonSchema.getVersion());
        schema.setTitle(mongoJsonSchema.getTitle());

        return schema;
    }
}
