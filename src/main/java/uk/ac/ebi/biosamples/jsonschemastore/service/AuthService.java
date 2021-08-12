package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;

@Slf4j
@Service
public class AuthService {
    private final SchemaStoreProperties properties;

    public AuthService(SchemaStoreProperties properties) {
        this.properties = properties;
    }

    public boolean login(String username, String password) {
        return (properties.getAdminUsername().equals(username) && properties.getAdminPassword().equals(password));
    }

    public boolean logout() {
        return true;
    }
}
