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

  @NonNull
  public ResponseEntity<ValidateResponseDocument[]> validate(@NonNull ValidateRequestDocument validateRequestDocument, @NonNull String hostURL) {
    try {
      RestTemplate restTemplate = new RestTemplate();
      URI uri = URI.create(hostURL);
      return restTemplate.postForEntity(uri, validateRequestDocument, ValidateResponseDocument[].class);
    } catch (Exception e) {
      log.error("Error occurred while validating JSON Object!", e);
      throw e;
    }
  }
}
