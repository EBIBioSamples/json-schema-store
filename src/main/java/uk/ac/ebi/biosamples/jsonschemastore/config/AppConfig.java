package uk.ac.ebi.biosamples.jsonschemastore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean(name = "fieldServiceObjectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
