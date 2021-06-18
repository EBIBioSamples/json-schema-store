package uk.ac.ebi.biosamples.jsonschemastore.ena;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.exception.ApplicationStateException;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaObjectPopulator;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChecklistConverterService {
    String enaChecklistBaseUrl = "https://www.ebi.ac.uk/ena/browser/api/xml/";

    private final SchemaService schemaService;

    public ChecklistConverterService(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    public String getChecklist(String checklistId) {
        String jsonSchema;
        try {
            EnaChecklist enaChecklist = getEnaChecklist(checklistId);
            List<Map<String, String>> properties = listProperties(enaChecklist);
            String schemaId = getSchemaId(enaChecklist);
            String title = enaChecklist.getChecklist().getDescriptor().getName();
            String description = enaChecklist.getChecklist().getDescriptor().getDescription();
            jsonSchema = SchemaTemplateGenerator.getBioSamplesSchema(schemaId, title, description, properties);
        } catch (Exception e) {
            log.error("Could not GET checklist: " + checklistId, e);
            throw new ApplicationStateException("Could not retrieve checklist for " + checklistId);
        }
        return jsonSchema;
    }

    public String saveSchema(String checklistId) {
        String checklist = getChecklist(checklistId);
        JsonNode schema = SchemaTemplateGenerator.getJson(checklist);

        JsonSchema jsonSchema = new JsonSchema();
        jsonSchema.setSchema(schema);
        jsonSchema.setAccession(checklistId);
        jsonSchema.setMetaSchema("https://schemablocks.org/meataschemas/jsonMetaSchema/1.0.1");
        SchemaObjectPopulator.populateSchemaRequestFields(jsonSchema);

        schemaService.saveSchemaWithAccession(jsonSchema);

        return checklist;
    }

    private EnaChecklist getEnaChecklist(String checklistId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EnaErrorHandler());
        URI uri = URI.create(enaChecklistBaseUrl + checklistId);
        EnaChecklist enaChecklist = restTemplate.getForObject(uri, EnaChecklist.class);
        Objects.requireNonNull(enaChecklist, "Failed to retrieve ENA checklist");

        return enaChecklist;
    }

    private String getSchemaId(EnaChecklist enaChecklist) {
        String schemaName = "ENA-" + enaChecklist.getChecklist().getDescriptor().getName().replace(" ", "_");
        String schemaVersion = "1.0.0";
        return String.format("https://www.ebi.ac.uk/biosamples/schemas/%s/%s", schemaName, schemaVersion);
    }

    private List<Map<String, String>> listProperties(EnaChecklist enaChecklist) {
        return enaChecklist.getChecklist().getDescriptor().getFieldGroups().stream().
                flatMap(group -> group.getFields().stream())
                .map(f -> Map.of("property", f.getName(),
                        "property_type", getTypedTemplate(f),
                        "property_description", f.getDescription(),
                        "requirement", f.getMandatory()))
                .collect(Collectors.toList());
    }

    private static String getTypedTemplate(Field field) {
        String fieldTypeTemplate;
        if (field.getFieldType().getTextChoiceField() != null) {
            fieldTypeTemplate = SchemaTemplateGenerator.getEnumTemplate(
                    field.getFieldType().getTextChoiceField().getTextValue()
                            .stream().map(TextValue::getValue).collect(Collectors.toList()));
        } else if (field.getFieldType().getTextField() != null) {
            String regex = field.getFieldType().getTextField().getRegex() != null ? field.getFieldType().getTextField().getRegex() : "";
            fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate(regex, 0, 0, "");
        } else {
            fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("", 0, 0, "");
        }

        return fieldTypeTemplate;
    }

    static class EnaErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return !clientHttpResponse.getStatusCode().equals(HttpStatus.OK);
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            throw new ApplicationStateException("Could not retrieve checklist: " + clientHttpResponse.getStatusText());
        }
    }
}
