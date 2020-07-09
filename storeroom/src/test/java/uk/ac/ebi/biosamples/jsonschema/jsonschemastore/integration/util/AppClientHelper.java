package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.integration.util;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AppClientHelper {

  public static final String AUTHORIZATION = "Authorization";
  private static final String BEARER = "Bearer ";

  private static final String appUrl = "https://explore.api.aai.ebi.ac.uk";
  private static final String userName = "hrashmi";
  private static final String password = "Tester123!";

  public static String getToken() {
    TestRestTemplate testRestTemplate = new TestRestTemplate();
    ResponseEntity<String> response =
        testRestTemplate
            .withBasicAuth(userName, password)
            .getForEntity(appUrl + "/auth", String.class);
    Assertions.assertEquals(
        HttpStatus.OK, response.getStatusCode(), "Getting Jwt was not succeed!");
    return BEARER + response.getBody();
  }
}
