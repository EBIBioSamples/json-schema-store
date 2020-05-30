package uk.ac.ebi.biosamples.jsonschemastore.client;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidateRequestDocument;
import uk.ac.ebi.biosamples.jsonschemastore.client.dto.ValidateResponseDocument;

import java.net.URI;

@Slf4j
public class ValidatorClient {

  private static final String BASE_URL = "http://localhost"; // TODO: consume application properties
  private static final String COLON = ":";
  private static final String PORT = "3000";
  private static final String VALIDATE_RESOURCE = "/validate";

  @NonNull
  public ResponseEntity<ValidateResponseDocument[]> validate(@NonNull ValidateRequestDocument validateRequestDocument) {
    try {
      RestTemplate restTemplate = new RestTemplate();
      URI uri = URI.create(BASE_URL + COLON + PORT + VALIDATE_RESOURCE);
      return restTemplate.postForEntity(uri, validateRequestDocument, ValidateResponseDocument[].class);
    } catch (Exception e) {
      log.error("Error occurred while validating JSON Object!", e);
      throw e;
    }
  }
}
