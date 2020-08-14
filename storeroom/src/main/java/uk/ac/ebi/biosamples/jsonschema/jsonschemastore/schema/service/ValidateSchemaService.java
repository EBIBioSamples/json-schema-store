package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.ValidatorClient;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto.ValidateRequestDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto.ValidateResponseDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.util.JsonSchemaMappingUtil;

@Service
public class ValidateSchemaService {

    private final ValidatorClient validatorClient;
    private final Environment environment;

    public ValidateSchemaService(ValidatorClient validatorClient, Environment environment) {
        this.validatorClient = validatorClient;
        this.environment = environment;
    }

    public ResponseEntity<ValidateResponseDocument> validateSchema(SchemaBlockDocument schema)
            throws JsonProcessingException {
        JsonNode jsonNode = JsonSchemaMappingUtil.convertSchemaBlockToJson(schema.getJsonSchema());
        ValidateRequestDocument validateRequestDocument = new ValidateRequestDocument();
        validateRequestDocument.setObject(jsonNode);
        validateRequestDocument.setSchema(JsonSchemaMappingUtil.getSchemaObject()); // TODO: meta schema should come from document store
        return validatorClient.validate(validateRequestDocument, environment.getProperty("elixirValidator.hostUrl"));
    }
}
