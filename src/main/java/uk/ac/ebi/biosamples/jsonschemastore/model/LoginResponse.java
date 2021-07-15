package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String token;
    private String error;
}
