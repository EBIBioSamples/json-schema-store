package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.model.*;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
public class SchemaValidationService {
    private final MetaSchemaService metaSchemaService;
    private final SchemaStoreProperties properties;

    public SchemaValidationService(MetaSchemaService metaSchemaService, SchemaStoreProperties properties) {
        this.metaSchemaService = metaSchemaService;
        this.properties = properties;
    }

    public List<ValidationError> validate(Schema schema) throws JsonSchemaServiceException {
        List<ValidationError> validationResponse = validate(schema, schema.getMetaSchema());

        if (!validationResponse.isEmpty()) {
            throw new JsonSchemaServiceException("Schema validation failed " + validationResponse);
        }

        return validationResponse;
    }

    public List<ValidationError> validate(Schema schema, String metaSchemaId) throws JsonSchemaServiceException {
        Optional<MetaSchema> metaSchema = metaSchemaService.getSchemaById(metaSchemaId);
        if (metaSchema.isEmpty()) {
            throw new JsonSchemaServiceException("Could not find meta-schema for $id: " + metaSchemaId);
        }

        return validate(schema, metaSchema.get());
    }

    public List<ValidationError> validate(Schema schema, MetaSchema metaSchema) {
        ValidationRequest validationRequest = new ValidationRequest(metaSchema.getSchema(), schema.getSchema());
        return postToValidator(validationRequest);
    }

    private List<ValidationError> postToValidator(@NonNull ValidationRequest validatorRequest) {
        String validatorUrl = Objects.requireNonNull(properties.getValidatorUrl());
        ValidationError[] validationResponse;
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = URI.create(validatorUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ValidationRequest> entity = new HttpEntity<>(validatorRequest, headers);
            validationResponse = restTemplate.postForEntity(uri, entity, ValidationError[].class).getBody();
        } catch (Exception e) {
            log.error("Error occurred while validating JSON Object!", e);
            throw e;
        }
        return Arrays.asList(validationResponse);
    }
}
