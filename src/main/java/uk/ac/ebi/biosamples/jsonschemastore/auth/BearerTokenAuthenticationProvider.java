package uk.ac.ebi.biosamples.jsonschemastore.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

//@Component
@RequiredArgsConstructor
public class BearerTokenAuthenticationProvider implements AuthenticationProvider {
    private final WebinTokenValidator webinTokenValidator;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!(authentication instanceof BearerTokenAuthenticationToken)) {
            return null;
        }

        String token = ((BearerTokenAuthenticationToken) authentication).getToken();
        UserDetails userDetails = webinTokenValidator.validateTokenAndGetUserDetails(token);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid token");
        }
        BearerTokenAuthenticationToken authenticatedToken = new BearerTokenAuthenticationToken(token) {
            @Override
            public Collection<GrantedAuthority> getAuthorities() {
                return userDetails.getAuthorities().stream()
                        .map(a->(GrantedAuthority)a).collect(Collectors.toList());
            }
        };
        authenticatedToken.setAuthenticated(true);
        authenticatedToken.setDetails(userDetails);
        return authenticatedToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
