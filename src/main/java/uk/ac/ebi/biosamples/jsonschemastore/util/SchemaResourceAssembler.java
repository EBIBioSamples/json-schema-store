package uk.ac.ebi.biosamples.jsonschemastore.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.controller.MetaSchemaController;
import uk.ac.ebi.biosamples.jsonschemastore.controller.SchemaController;
import uk.ac.ebi.biosamples.jsonschemastore.controller.SchemaExporterController;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchemaResourceAssembler {
    private final PagedResourcesAssembler<JsonSchema> pagedResourcesAssembler;
    private final PagedResourcesAssembler<MongoJsonSchema> mongoJsonSchemaResourcesAssembler;
    private final PagedResourcesAssembler<MetaSchema> pagedResourcesAssemblerForMetaSchema;
    private final PagedResourcesAssembler<SchemaOutline> pagedResourcesAssemblerForSchemaOutline;
    private final EntityLinks entityLinks;
    private final SchemaObjectPopulator schemaObjectPopulator;

    public JsonSchema populateResources(JsonSchema schema) {
        return (JsonSchema) schema.add(linkTo(methodOn(SchemaController.class).getSchema(schema.getId())).withSelfRel());
    }

    public MetaSchema populateResources(MetaSchema schema) {
        return (MetaSchema) schema.add(linkTo(methodOn(MetaSchemaController.class).getMetaSchema(schema.getId())).withSelfRel());
    }

    public SchemaOutline populateResources(SchemaOutline schema) {
        return schema.add(linkTo(methodOn(SchemaController.class).getSchema(schema.getId())).withSelfRel());
    }

    public PagedModel<EntityModel<JsonSchema>> buildPage(Page<JsonSchema> page) {
        page.stream().forEach(this::populateResources);
        return pagedResourcesAssembler.toModel(page);
    }

    public PagedModel<EntityModel<MetaSchema>> buildPageForMetaSchema(Page<MetaSchema> page) {
        page.stream().forEach(this::populateResources);
        return pagedResourcesAssemblerForMetaSchema.toModel(page);
    }

    public PagedModel<EntityModel<SchemaOutline>> buildPageForSchemaOutline(Page<SchemaOutline> page) {
        page.stream().forEach(this::populateResources);
        return pagedResourcesAssemblerForSchemaOutline.toModel(page);
    }

    public PagedModel<EntityModel<MongoJsonSchema>> buildMongoJsonSchemaPage(Page<MongoJsonSchema> entityPage) {
        return mongoJsonSchemaResourcesAssembler.toModel(entityPage, schema -> {
            EntityModel<MongoJsonSchema> schemaEntityModel = EntityModel.of(schema)
                    .add(entityLinks.linkToItemResource(MongoJsonSchema.class, schema.getId()).withSelfRel());
            try {
                Link schemaEntityModelWithJsonSchemaLink = buildJsonSchemaLink(schema);
                schemaEntityModel.add(schemaEntityModelWithJsonSchemaLink);
            } catch (Exception e) {
                log.error("cannot add json-schema link to schema {}", schema.getId(), e);
            }
            return schemaEntityModel;
        });
    }

    private Link buildJsonSchemaLink(MongoJsonSchema schema) {
        return Link.of(schemaObjectPopulator.getSchemaResourceURL(schema.getId()))
                        .withRel("json-schema");
    }

}

