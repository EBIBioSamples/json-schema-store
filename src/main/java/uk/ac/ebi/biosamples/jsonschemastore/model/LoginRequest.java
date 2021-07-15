package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
