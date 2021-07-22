package uk.ac.ebi.biosamples.jsonschemastore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.exception.MalformedSchemaException;
import uk.ac.ebi.biosamples.jsonschemastore.model.Checklist;
import uk.ac.ebi.biosamples.jsonschemastore.model.ChecklistField;

import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChecklistService {
    private final ObjectMapper mapper;
    private final VelocityEngine vEngine;

    public ChecklistService() {
        mapper = new ObjectMapper();
        vEngine = new VelocityEngine();
        vEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        vEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        vEngine.init();
    }

    public Checklist getChecklist(String id) {
        return null;
    }

    public String checklistToSchema(Checklist checklist) {
        String jsonSchema = populateSchema(checklist);

        try {
            Object o = mapper.readValue(jsonSchema, Object.class);
            jsonSchema = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert to JSON", e);
            e.printStackTrace();
            throw new MalformedSchemaException("String to JSON conversion error.", e);
        }

        return jsonSchema;
    }

    public Checklist schemaToChecklist(String schema) {
        //https://stackoverflow.com/questions/32307647/i-want-to-extract-the-value-of-a-variable-from-different-file-in-velocity-templa
        return null;
    }

    public boolean validate(Checklist checklist) {
        return true;
    }

    private String populateSchema(Checklist checklist) {
        String fields = populateSchemaFields(checklist.getFields(), getFieldTemplate(checklist.getMetaSchema()));
        List<String> required = populateRequiredFields(checklist.getFields());
        List<String> recommended = populateRecommendedFields(checklist.getFields());

        StringWriter writer = new StringWriter();
        Template template = getTemplate(checklist.getMetaSchema());
        VelocityContext ctx = new VelocityContext();
        ctx.put("schema_id", checklist.getSchemaId());
        ctx.put("schema_title", checklist.getTitle());
        ctx.put("schema_description", checklist.getDescription());
        ctx.put("properties", fields);
        ctx.put("required", new JSONArray(required));
        ctx.put("recommended", new JSONArray(recommended));
        template.merge(ctx, writer);

        return writer.toString();
    }

    private String populateSchemaFields(List<ChecklistField> checklistFields, Template template) {
        StringWriter writer = new StringWriter();
        for (ChecklistField field : checklistFields) {
            VelocityContext ctx = new VelocityContext();
            ctx.put("property", field.getName());
            ctx.put("property_type", populateFieldType(field));
            ctx.put("property_description", field.getDescription());
            template.merge(ctx, writer);
            writer.append(",\n");
        }
        String properties = writer.toString();
        properties = properties.substring(0, properties.length() - 2); //remove last comma

        return properties;
    }

    private List<String> populateRequiredFields(List<ChecklistField> checklistFields) {
        return checklistFields.stream()
                .filter(ChecklistField::isRequired)
                .map(ChecklistField::getName)
                .collect(Collectors.toList());
    }

    private List<String> populateRecommendedFields(List<ChecklistField> checklistFields) {
        return checklistFields.stream()
                .filter(ChecklistField::isRecommended)
                .map(ChecklistField::getName)
                .collect(Collectors.toList());
    }

    private Template getTemplate(String metaSchema) {
        String templatePath = "";
        Template template = vEngine.getTemplate("templates/biosamples_template.vm");

        return Objects.requireNonNull(template);
    }

    private Template getFieldTemplate(String metaSchema) {
        String templatePath = "";
        Template template = vEngine.getTemplate("templates/biosamples_property_template.json");
        return Objects.requireNonNull(template);
    }


    private String populateFieldType(ChecklistField field) {
        String fieldTypeTemplate;
        if (field.getType().equalsIgnoreCase("enum")) {
            fieldTypeTemplate = getEnumTemplate(field);
        } else if (field.getType().equalsIgnoreCase("string")) {
            fieldTypeTemplate = getStringTemplate(field);
        } else if (field.getType().equalsIgnoreCase("number") || field.getType().equalsIgnoreCase("integer")) {
            fieldTypeTemplate = getNumberTemplate(field);
        } else if (field.getType().equalsIgnoreCase("boolean")) {
            fieldTypeTemplate = getBooleanTemplate();
        } else if (field.getType().equalsIgnoreCase("const")) {
            fieldTypeTemplate = getConstTemplate();
        } else {
            throw new MalformedSchemaException("Unknown checklist field type: " + field.getType());
        }

        return fieldTypeTemplate;
    }

    public String getStringTemplate(ChecklistField field) {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("type", "string");
        if (field.getPattern() != null && !field.getPattern().isEmpty()) {
            template.put("pattern", field.getPattern());
        }
        if (field.getFormat() != null && !field.getFormat().isEmpty()) {
            template.put("format", field.getFormat());
        }
        if (field.getMinLength() > 0) {
            template.put("minLength", field.getMinLength());
        }
        if (field.getMaxLength() > 0) {
            template.put("maxLength", field.getMaxLength());
        }

        return getJsonString(template);
    }

    public String getEnumTemplate(ChecklistField field) {
        List<String> enums = Arrays.stream(field.getEnums().split(","))
                .map(String::trim)
                .collect(Collectors.toList());
        return getJsonString(Map.of("enum", enums));
    }

    public String getNumberTemplate(ChecklistField field) {
        Map<String, Object> template = new LinkedHashMap<>();
        if (field.getType().equalsIgnoreCase("integer")) {
            template.put("type", "integer");
        } else {
            template.put("type", "number");
        }

        if (field.getMultiples() > 0) {
            template.put("multipleOf", field.getMultiples());
        }
        if (field.getMin() > 0) {
            template.put("minimum", field.getMin());
        }
        if (field.getMax() > 0) {
            template.put("maximum", field.getMax());
        }

        //template.put("exclusiveMinimum", field.getMin());
        //template.put("exclusiveMaximum", field.getMax());

        return getJsonString(template);
    }

    private String getBooleanTemplate() {
        return getJsonString(Map.of("type", "boolean"));
    }

    private String getConstTemplate() {
        return getJsonString(Map.of("type", "const"));
    }

    public String getJsonString(Object o) {
        String template;
        try {
            template = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Failed to write JSON string, " + e.getMessage());
            throw new MalformedSchemaException("Failed to write JSON string, " + e.getMessage());
        }
        return template;
    }
}
