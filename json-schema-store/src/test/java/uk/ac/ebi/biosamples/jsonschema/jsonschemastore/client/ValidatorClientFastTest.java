package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.client.dto.ValidateRequestDocument;

class ValidatorClientFastTest {

  @Test()
  public void testValidateError() {
    ValidatorClient validatorClient = new ValidatorClient();
    Assertions.assertThrows(
        Exception.class,
        () ->
            validatorClient.validate(
                new ValidateRequestDocument(), "http://localhost/validate:3000"));
  }
}
