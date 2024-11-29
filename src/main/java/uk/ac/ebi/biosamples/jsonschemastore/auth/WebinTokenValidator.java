package uk.ac.ebi.biosamples.jsonschemastore.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.model.User;
import uk.ac.ebi.biosamples.jsonschemastore.service.UserService;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebinTokenValidator implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    final RestTemplate restTemplate;
    final SchemaStoreProperties schemaStoreProperties;
    final ObjectMapper objectMapper;
    final UserService userService;


    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        String tokenString = (String) token.getPrincipal();
        try {
            // Send the token to the third-party API for validation
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, tokenString);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            String validationUrl = schemaStoreProperties.getAuthApiUrl() + "/admin/submission-account";

            ResponseEntity<String> authResponse = restTemplate.exchange(
                    new URI(validationUrl), HttpMethod.GET, requestEntity, String.class
            );
            if (authResponse.getStatusCode().is2xxSuccessful()) {
                JsonNode accountInfo = objectMapper.readTree(authResponse.getBody());
                String submissionAccountId = accountInfo.get("submissionAccountId").asText();
                Collection<? extends GrantedAuthority> authorities = getGrantedAuthorities(submissionAccountId);
                return User.builder()
                        .username(submissionAccountId)
                        .password("N/A")
                        .enabled("N".equals(accountInfo.get("suspended").asText()))
                        .accountNonLocked("N".equals(accountInfo.get("suspended").asText()))
                        .accountNonExpired("N".equals(accountInfo.get("suspended").asText()))
                        .authorities(authorities)
                        .build();
            } else {
                throw new BadCredentialsException("Invalid token. auth response status: " + authResponse.getStatusCode());
            }
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid token. Error during validation: " + ex.getMessage(), ex);
        }
    }


    // TODO: this belongs in UserService
    private Collection<? extends GrantedAuthority> getGrantedAuthorities(String submissionAccountId) {
        UserDetails userDetails = null;
        Collection<? extends GrantedAuthority> defaultAuthorities = null;
        try {
            userDetails = userService.loadUserByUsername(submissionAccountId);
        } catch (AuthenticationCredentialsNotFoundException e) {
            defaultAuthorities = List.of(new SimpleGrantedAuthority("reader"));
        }

        return Optional.ofNullable(userDetails).map(UserDetails::getAuthorities)
                .orElse(defaultAuthorities);
    }
}
