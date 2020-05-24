package uk.ac.ebi.biosamples.jsonschemastore.client;

import lombok.extern.log4j.Log4j;
import org.springframework.web.client.RestTemplate;

@Log4j
public class ValidatorClient {

    private static String BASE_URL = "http://localhost:3020/validate";
    public void validate(ValidateSchemaDTO validateSchemaDTO) {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.postForObject(BASE_URL, validateSchemaDTO, String.class);

    }
}
