package uk.ac.ebi.biosamples.jsonschemastore.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class SchemaStoreProperties {

    @Value("${schemastore.authority:BIOSAMPLES}")
    private String defaultAuthority;

    @Value("${schemastore.validator.url:http://localhost:3020/validate}")
    private String validatorUrl;
}
