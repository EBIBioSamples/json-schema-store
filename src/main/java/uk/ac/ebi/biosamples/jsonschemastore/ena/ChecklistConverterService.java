package uk.ac.ebi.biosamples.jsonschemastore.ena;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.exception.ApplicationStateException;
import uk.ac.ebi.biosamples.jsonschemastore.model.*;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.Multiplicity;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.service.SchemaService;
import uk.ac.ebi.biosamples.jsonschemastore.util.SchemaObjectPopulator;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistConverterService {
    private static final String enaGetAllChecklistUrl = "https://www.ebi.ac.uk/ena/submit/report/checklists?type=sample&format=json";
    private static final String enaChecklistBaseUrl = "https://www.ebi.ac.uk/ena/submit/report/checklists/xml/${checklistId}?type=sample";

    private final SchemaService schemaService;
    private final SchemaObjectPopulator populator;
    private final SchemaTemplateGenerator schemaTemplateGenerator;

    private static String getTypedTemplate(Field field) {
        String fieldTypeTemplate;
        if (field.getFieldType().getTextChoiceField() != null) {
            fieldTypeTemplate = SchemaTemplateGenerator.getEnumTemplate(getEnumValueList(field.getFieldType().getTextChoiceField()));
        } else if (field.getFieldType().getTextField() != null) {
            String regex = field.getFieldType().getTextField().getRegex() != null ? field.getFieldType().getTextField().getRegex() : "";
            fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("pattern", regex, 0, 0, "");
        } else if (field.getFieldType().getOntologyField() != null) {
            String ontologyId = field.getFieldType().getOntologyField().getOntologyId() != null ? field.getFieldType().getOntologyField().getOntologyId() : "";
            fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("ontology", ontologyId, 0, 0, "");
        } else if (field.getFieldType().getTaxonField() != null) {
            fieldTypeTemplate = SchemaTemplateGenerator.getTaxonTemplate();
        } else {
            fieldTypeTemplate = SchemaTemplateGenerator.getStringTemplate("","", 0, 0, "");
        }

        return fieldTypeTemplate;
    }

    private static List<String> getEnumValueList(TextChoiceField enumField) {
        List<String> values = new ArrayList<>();
        for (TextValue textValue : enumField.getTextValue()) {
            values.add(textValue.getValue());
            if (textValue.getSynonyms() != null) {
                values.addAll(textValue.getSynonyms());
            }
        }
        return values;
    }

    private static Property mapEnaFieldToProperty(Field field) {
        return new Property(field.getName(),
                field.getSynonyms(),
                field.getDescription(),
                getTypedTemplate(field),
                field.getUnits(),
                getCardinality(field.getMandatory()),
                getMultiplicity(field.getMultiplicity()),
                field.getGroupName()
        );
    }

    private static Property.AttributeCardinality getCardinality(String fieldRequirement) {
        if (StringUtils.hasText(fieldRequirement)) {
            return Property.AttributeCardinality.valueOf(fieldRequirement.toUpperCase());
        } else {
            return Property.AttributeCardinality.OPTIONAL;
        }
    }

    private static Property.Multiplicity getMultiplicity(String multiplicity) {
        if (StringUtils.hasText(multiplicity)) {
            return Property.Multiplicity.valueOf(multiplicity.toUpperCase());
        } else {
            return Property.Multiplicity.SINGLE;
        }
    }

    private static SchemaFieldAssociation fieldAssociationFromProperty(Property property) {
        return new SchemaFieldAssociation(
                new FieldId(toVariableName(property.name()), "1.0").asString(),
                property.cardinality(),
                Multiplicity.Single);
    }

    public ImportedChecklist convertEnaChecklist(String checklistId) {
        try {
            List<Property> properties;
            EnaChecklist enaChecklist = getEnaChecklist(checklistId);
            properties = listProperties(enaChecklist);
                String schemaId = new SchemaId(enaChecklist.getChecklist().getAccession(), "1.0").asString();
            String title = enaChecklist.getChecklist().getDescriptor().getName();
            String description = enaChecklist.getChecklist().getDescriptor().getDescription();
            String jsonSchema = schemaTemplateGenerator.getBioSamplesSchema(schemaId, title, description, properties, false);
            return new ImportedChecklist(jsonSchema, properties);
        } catch (Exception e) {
            log.error("Could not convert checklist: " + checklistId, e);
            throw new ApplicationStateException("Could not convert checklist for " + checklistId, e);
        }
    }

    public String persistEnaChecklist(String checklistId) {
        ImportedChecklist jsonSchema = convertEnaChecklist(checklistId);
        saveSchema(checklistId, jsonSchema);
        return jsonSchema.getJsonSchemaString();
    }

    public String persistEnaChecklists() {
        List<String> accessions = getEnaChecklists();
        List<String> checklists = new ArrayList<>();
        for (String accession : accessions) {
            checklists.add(persistEnaChecklist(accession));
        }
        return checklists.toString();
    }

    public String saveSchema(String checklistId, ImportedChecklist importedChecklist) {

        JsonSchema jsonSchema = fromJson(checklistId, importedChecklist);
        Set<uk.ac.ebi.biosamples.jsonschemastore.model.Field> fields = importedChecklist.getProperties()
                .stream().map(uk.ac.ebi.biosamples.jsonschemastore.model.Field::fromProperty)
                .collect(Collectors.toSet());

        schemaService.saveSchemaWithAccession(jsonSchema, fields);

        return importedChecklist.getJsonSchemaString();
    }

    private JsonSchema fromJson(String checklistId, ImportedChecklist importedChecklist) {
        JsonNode schemaAsJson = SchemaTemplateGenerator.getJson(importedChecklist.getJsonSchemaString());

        JsonSchema jsonSchema = new JsonSchema();
        jsonSchema.setSchema(schemaAsJson);
        jsonSchema.setVersion("1.0");
        jsonSchema.setAuthority(Authority.ENA.name());
        jsonSchema.setAccession(checklistId);
        jsonSchema.setMetaSchema("https://schemablocks.org/metaschemas/json-schema-draft-07/1.0.1");
        jsonSchema.setEditable(true);
        jsonSchema.setLatest(true);
        importedChecklist.getProperties()
                .stream().map(ChecklistConverterService::fieldAssociationFromProperty)
                .forEach(jsonSchema.getSchemaFieldAssociations()::add);
        populator.populateSchema(jsonSchema);
        return jsonSchema;
    }

    private EnaChecklist getEnaChecklist(String checklistId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EnaErrorHandler());
        URI uri = URI.create(enaChecklistBaseUrl.replace("${checklistId}", checklistId));
        EnaChecklist enaChecklist = restTemplate.getForObject(uri, EnaChecklist.class);
        Objects.requireNonNull(enaChecklist, "Failed to retrieve ENA checklist" + checklistId);

        return enaChecklist;
    }

    private static List<String> getEnaChecklists() {
        List<String> enaChecklists = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EnaErrorHandler());
        URI uri = URI.create(enaGetAllChecklistUrl);
        JsonNode allChecklistsJson = restTemplate.getForObject(uri, JsonNode.class);
        Objects.requireNonNull(allChecklistsJson, "Failed to retrieve ENA checklist");

        if (allChecklistsJson.isArray()) {
            for (JsonNode n : allChecklistsJson) {
                enaChecklists.add(n.get("report").get("id").textValue());
            }
        }

        return enaChecklists;
    }

    private List<Property> listProperties(EnaChecklist enaChecklist) {
        return enaChecklist.getChecklist().getDescriptor()
                .getFieldGroups().stream()
                .flatMap(group -> group.getFields().stream())
                .map(ChecklistConverterService::mapEnaFieldToProperty)
                .collect(Collectors.toList());
    }

    static class EnaErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
            return !clientHttpResponse.getStatusCode().equals(HttpStatus.OK);
        }

        @Override
        public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
            throw new ApplicationStateException("Could not retrieve checklist: " + clientHttpResponse);
        }
    }
}
