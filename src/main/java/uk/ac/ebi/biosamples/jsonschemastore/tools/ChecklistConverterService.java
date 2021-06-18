package uk.ac.ebi.biosamples.jsonschemastore.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChecklistConverterService {
    public EnaChecklist getChecklist(String checklistId) {
        checklistId = "ERC000012";
        return getEnaChecklist1(checklistId);
    }

    public String getEnaChecklist(String checklistId) {
        String enaChecklistUrl = "https://www.ebi.ac.uk/ena/browser/api/xml/" + checklistId;
        String jsonSchema;
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = URI.create(enaChecklistUrl);
            EnaChecklist enaChecklist = restTemplate.getForObject(uri, EnaChecklist.class);

            List<Map<String, String>> filedNames = enaChecklist.getChecklist().getDescriptor().getFieldGroups().stream().
                    flatMap(group -> group.getFields().stream())
                    .map(f -> Map.of("property", f.getName(), "property_type", getWithTypeInfo(f)))
                    .collect(Collectors.toList());

            String schemaId = "https://www.ebi.ac.uk/biosamples/schemas/" + checklistId;
            jsonSchema = getBioSamplesSchema(schemaId, filedNames);

        } catch (Exception e) {
            log.error("Could not GET checklist: " + checklistId, e);
            throw e;
        }
        return jsonSchema;
    }


    private EnaChecklist getEnaChecklist1(String checklistId) {
        String enaChecklistUrl = "https://www.ebi.ac.uk/ena/browser/api/xml/" + checklistId;
        EnaChecklist enaChecklist;
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = URI.create(enaChecklistUrl);
            enaChecklist = restTemplate.getForObject(uri, EnaChecklist.class);

            List<Map<String, String>> filedNames = enaChecklist.getChecklist().getDescriptor().getFieldGroups().stream().
                    flatMap(group -> group.getFields().stream())
                    .map(f -> Map.of("property", f.getName(), "property_type", getWithTypeInfo(f)))
                    .collect(Collectors.toList());

            String jsonSchema = getBioSamplesSchema(checklistId, filedNames);

            Map<String, String> propertyMap = new HashMap<>();
            propertyMap.put("property", "");
            propertyMap.put("property_type", "");

            System.out.println("Number of fields: " + filedNames.size());

            System.out.println(enaChecklist.getChecklist().getAccession());
            System.out.println(enaChecklist.getChecklist().getDescriptor().getFieldGroups().get(0).getFields().get(0));
        } catch (Exception e) {
            log.error("Could not GET checklist: " + checklistId, e);
            throw e;
        }
        return enaChecklist;
    }

    private String getBioSamplesSchema(String schemaId, List<Map<String, String>> properties) {
//        return SchemaTemplateGenerator.test();
        return SchemaTemplateGenerator.getBioSamplesSchema(schemaId, properties);
    }

    private static String getWithTypeInfo(Field field) {
        String fieldTypeTemplate;
        if (field.getFieldType().getTextChoiceField() != null) {
            fieldTypeTemplate = SchemaTemplateGenerator.getEnumTemplate(
                    field.getFieldType().getTextChoiceField().getTextValue()
                            .stream().map(TextValue::getValue).collect(Collectors.toList()));
        } else if (field.getFieldType().getTextField() != null) {
            String regex = field.getFieldType().getTextField().getRegex() != null ? field.getFieldType().getTextField().getRegex() : "";
            fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate(regex, 0, 0);
        } else {
            fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("", 0, 0);
        }

        return fieldTypeTemplate;
    }
}
