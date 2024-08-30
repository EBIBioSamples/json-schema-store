package uk.ac.ebi.biosamples.jsonschemastore.ena;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.biosamples.jsonschemastore.exception.MalformedSchemaException;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;

import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SchemaTemplateGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String getBioSamplesSchema(String schemaId, String title,
                                             String description, List<Property> propertyList) {
        VelocityEngine vEngine = new VelocityEngine();
        vEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        vEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        vEngine.init();

        // Collect all the attributes with requirements
        List<Set<String>> required = propertyList.stream()
            .filter(p -> p.cardinality() == Property.AttributeCardinality.MANDATORY)
            .map(p -> {
                Set<String> s = new HashSet<>();
                s.add(p.name());
                if (!CollectionUtils.isEmpty(p.synonyms())) {
                    s.addAll(p.synonyms());
                }
                return s;
            })
            .collect(Collectors.toList());
        List<List<String>> recommended = propertyList.stream()
            .filter(p -> p.cardinality() == Property.AttributeCardinality.RECOMMENDED)
            .map(Property::synonyms)
            .collect(Collectors.toList());

        List<Property> propertiesWithSynonyms = propertyList.stream()
            .flatMap(p -> {
                List<Property> props = new ArrayList<>();
                props.add(p);
                if (!CollectionUtils.isEmpty(p.synonyms())) {
                    props.addAll(
                        p.synonyms().stream()
                            .map(s -> new Property(s, Collections.emptyList(), p.description(), p.type(), p.units(), p.cardinality()))
                            .toList());
                }
                return props.stream();
        }).toList();

        // Write everything to main template
        StringWriter schemaWriter = new StringWriter();
        Template template = vEngine.getTemplate("templates/biosamples_template.vm");
        VelocityContext ctx = new VelocityContext();
        ctx.put("schema_id", schemaId);
        ctx.put("schema_title", title);
        ctx.put("schema_description", description);
        ctx.put("properties", propertiesWithSynonyms);
        ctx.put("required", required);
        ctx.put("recommended", recommended);
        template.merge(ctx, schemaWriter);

        return prettyPrintJson(schemaWriter.toString());
    }

    public static String getStringTemplate(String regex, int minLength, int maxLength, String format) {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("type", "string");
        if (regex != null && !regex.isEmpty()) {
            template.put("pattern", regex);
        }
        if (format != null && !format.isEmpty()) {
            template.put("format", format);
        }
        if (minLength > 0) {
            template.put("minLength", minLength);
        }
        if (maxLength > 0) {
            template.put("maxLength", maxLength);
        }

        return getJsonString(template);
    }

    public static String getEnumTemplate(List<String> enums) {
        return getJsonString(Map.of("enum", enums));
    }

    public static String getNumberTemplate(double multipleOf, double minimum,
                                           double maximum, double exclusiveMinimum, double exclusiveMaximum) {
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("type", "number");
        template.put("multipleOf", multipleOf);
        template.put("minimum", minimum);
        template.put("exclusiveMinimum", exclusiveMinimum);
        template.put("maximum", maximum);
        template.put("exclusiveMaximum", exclusiveMaximum);

        return getJsonString(template);
    }

    public static String getIntegerTemplate() {
        return getJsonString(Map.of("type", "integer"));
    }

    public static String getJsonString(Object o) {
        String template;
        try {
            template = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Failed to write JSON string, " + e.getMessage());
            throw new MalformedSchemaException("Failed to write JSON string, " + e.getMessage());
        }
        return template;
    }

    public static JsonNode getJson(String s) {
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readValue(s, JsonNode.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to write string to JSON, " + e.getMessage());
            throw new MalformedSchemaException("Failed to write string to JSON, " + e.getMessage());
        }
        return jsonNode;
    }

    public static String prettyPrintJson(String jsonString) {
        String prttyJsonString;
        try {
            Object o = mapper.readValue(jsonString, Object.class);
            prttyJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse string, malformed JSON", e);
            throw new MalformedSchemaException("Failed to parse JSON, " + e.getMessage());
        }
        return prttyJsonString;
    }
}
