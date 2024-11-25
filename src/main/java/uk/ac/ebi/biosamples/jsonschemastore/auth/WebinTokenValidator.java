package uk.ac.ebi.biosamples.jsonschemastore.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class WebinTokenValidator {
    final RestTemplate restTemplate;
    final SchemaStoreProperties schemaStoreProperties;
    final ObjectMapper objectMapper;


    UserDetails validateTokenAndGetUserDetails(String token) {
        try {
            // Send the token to the third-party API for validation
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            String validationUrl = schemaStoreProperties.getAuthApiUrl() + "/admin/submission-account";

            ResponseEntity<String> authResponse = restTemplate.exchange(
                    new URI(validationUrl), HttpMethod.GET, requestEntity, String.class
            );
            if (authResponse.getStatusCode().is2xxSuccessful()) {

                JsonNode accountInfo = objectMapper.readTree(authResponse.getBody());
                return User.builder()
                        .username(accountInfo.get("submissionAccountId").asText())
                        .password("")
                        .disabled(!"N".equals(accountInfo.get("suspended").asText()))
                        .accountLocked(!"N".equals(accountInfo.get("suspended").asText()))
                        .authorities("reader")
                        .build();
            } else {
                throw new BadCredentialsException("Invalid token. auth response status: " + authResponse.getStatusCode());
            }
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid token. Error during validation", ex);
        }
    }
}
