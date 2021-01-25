package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.exception.JsonSchemaServiceException;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.Schema;
import uk.ac.ebi.biosamples.jsonschemastore.model.ValidationRequest;
import uk.ac.ebi.biosamples.jsonschemastore.model.ValidationResponse;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class SchemaValidationService {
    private final Environment environment;
    private final MetaSchemaService metaSchemaService;

    public SchemaValidationService(MetaSchemaService metaSchemaService, Environment environment) {
        this.metaSchemaService = metaSchemaService;
        this.environment = environment;
    }

    public ValidationResponse validate(Schema schema) throws JsonSchemaServiceException {
        ValidationResponse validationResponse = validate(schema, schema.getMetaSchema());

        if (!validationResponse.getValidationErrors().isEmpty()) {
            throw new JsonSchemaServiceException("Schema validation failed " + validationResponse.getValidationErrors());
        }

        return validationResponse;
    }

    public ValidationResponse validate(Schema schema, String metaSchemaId) throws JsonSchemaServiceException {
        Optional<MetaSchema> metaSchema = metaSchemaService.getSchemaById(metaSchemaId);
        if (metaSchema.isEmpty()) {
            throw new JsonSchemaServiceException("Could not find meta-schema for $id: " + metaSchemaId);
        }

        return validate(schema, metaSchema.get());
    }

    public ValidationResponse validate(Schema schema, MetaSchema metaSchema) {
        ValidationRequest validationRequest = new ValidationRequest(metaSchema.getSchema(), schema.getSchema());
        return postToValidator(validationRequest);
    }

    private ValidationResponse postToValidator(@NonNull ValidationRequest validatorRequest) {
        String validatorUrl = Objects.requireNonNull(environment.getProperty("elixirValidator.hostUrl"));
        ValidationResponse validationResponse;
        try {
            RestTemplate restTemplate = new RestTemplate();
            URI uri = URI.create(validatorUrl);
            validationResponse = restTemplate.postForEntity(uri, validatorRequest, ValidationResponse.class).getBody();
        } catch (Exception e) {
            log.error("Error occurred while validating JSON Object!", e);
            throw e;
        }
        return validationResponse;
    }
}
