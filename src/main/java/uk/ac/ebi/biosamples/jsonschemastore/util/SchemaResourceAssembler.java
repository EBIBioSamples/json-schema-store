package uk.ac.ebi.biosamples.jsonschemastore.util;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;
import uk.ac.ebi.biosamples.jsonschemastore.resource.MetaSchemaController;
import uk.ac.ebi.biosamples.jsonschemastore.resource.SchemaController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SchemaResourceAssembler {
    private final PagedResourcesAssembler<JsonSchema> pagedResourcesAssembler;
    private final PagedResourcesAssembler<MetaSchema> pagedResourcesAssemblerForMetaSchema;
    private final PagedResourcesAssembler<SchemaOutline> pagedResourcesAssemblerForSchemaOutline;

    public SchemaResourceAssembler(PagedResourcesAssembler<JsonSchema> pagedResourcesAssembler,
                                   PagedResourcesAssembler<SchemaOutline> pagedResourcesAssemblerForSchemaOutline,
                                   PagedResourcesAssembler<MetaSchema> pagedResourcesAssemblerForMetaSchema) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.pagedResourcesAssemblerForMetaSchema = pagedResourcesAssemblerForMetaSchema;
        this.pagedResourcesAssemblerForSchemaOutline = pagedResourcesAssemblerForSchemaOutline;
    }

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
}
