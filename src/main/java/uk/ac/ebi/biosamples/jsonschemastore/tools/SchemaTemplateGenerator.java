package uk.ac.ebi.biosamples.jsonschemastore.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaTemplateGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) {
        System.out.println(getStringTemplate("*", 0, 0));
        test();
    }

    public static String getBioSamplesSchema(String schemaId, List<Map<String, String>> propertyList) {
        VelocityEngine vEngine = new VelocityEngine();
        vEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        vEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        vEngine.init();

        StringWriter wrt = new StringWriter();
        Template t = vEngine.getTemplate("templates/biosamples_property_template.json");
        for (Map<String, String> p : propertyList) {
            VelocityContext ctx = new VelocityContext();
            ctx.put("property", p.get("property"));
            ctx.put("property_type", p.get("property_type"));
            t.merge(ctx, wrt);
            wrt.append(",\n");
        }

        String properties = wrt.toString();

        wrt = new StringWriter();
        t = vEngine.getTemplate("templates/biosamples_template.json");
        VelocityContext ctx = new VelocityContext();
        ctx.put("schema_id", schemaId);
        ctx.put("properties", properties);
        ctx.put("required", new JSONArray(Arrays.asList("organism")));
        t.merge(ctx, wrt);

        return wrt.toString();
    }

    public static String test() {
        VelocityEngine vEngine = new VelocityEngine();
        vEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        vEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        vEngine.init();

        StringWriter wrt = new StringWriter();
        Template t = vEngine.getTemplate("templates/biosamples_property_template.json");
        VelocityContext ctx = new VelocityContext();
        ctx.put("property", "organism");
        ctx.put("property_type", getStringTemplate("*", 0, 0));
        t.merge(ctx, wrt);
        wrt.append(",\n");

        ctx = new VelocityContext();
        ctx.put("property", "description");
        ctx.put("property_type", getStringTemplate("", 1, 100));
        t.merge(ctx, wrt);

        String properties = wrt.toString();

        wrt = new StringWriter();
        t = vEngine.getTemplate("templates/biosamples_template.json");
        ctx = new VelocityContext();
        ctx.put("schema_id", "https://www.ebi.ac.uk/biosamples/schemas/biosamples-minimal/1.0.0");
        ctx.put("properties", properties);
        ctx.put("required", new JSONArray(Arrays.asList("organism")));
        t.merge(ctx, wrt);

        System.out.print(wrt.toString());
        return wrt.toString();
    }

    public static String getStringTemplate(String regex, int minLength, int maxLength) {
        Map<String, Object> template = new HashMap<>();
        template.put("type", "string");
        if (regex != null && !regex.isEmpty()) {
            template.put("pattern", regex);
        }
//        if (format != null && !regex.isEmpty()) {
//            template.put("format", format);
//        }
        if (minLength > 0) {
            template.put("minLength", minLength);
        }
        if (maxLength > 0) {
            template.put("maxLength", maxLength);
        }

        return getJsonString(template);
    }

    public static String getEnumTemplate(List<String> enums) {
        Map<String, Object> template = new HashMap<>();
        template.put("type", "string");
        template.put("enum", enums);

        return getJsonString(template);
    }

    public static String getNumberTemplate(double multipleOf, double minimum,
                                           double maximum, double exclusiveMinimum, double exclusiveMaximum) {
        Map<String, Object> template = new HashMap<>();
        template.put("type", "number");
        template.put("multipleOf", 0);
        template.put("minimum", 0);
        template.put("exclusiveMinimum", 0);
        template.put("maximum", 0);
        template.put("exclusiveMaximum", 0);

        return getJsonString(template);
    }


    public static String getIntTemplate() {
        Map<String, Object> template = new HashMap<>();
        template.put("type", "integer");

        return getJsonString(template);
    }


    public static String getJsonString(Object o) {
        String template;
        try {
            template = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            template = "";
        }

        return template;
    }
}
