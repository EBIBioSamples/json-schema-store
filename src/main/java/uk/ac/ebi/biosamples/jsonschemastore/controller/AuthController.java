package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.biosamples.jsonschemastore.model.LoginRequest;
import uk.ac.ebi.biosamples.jsonschemastore.model.LoginResponse;
import uk.ac.ebi.biosamples.jsonschemastore.service.AuthService;

//todo This is just pony auth methods, create proper login functionality

@Slf4j
@Controller
@RequestMapping(value = "/api/v2/auth", produces = {"application/json"})
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse();
        if (authService.login(request.getUsername(), request.getPassword())) {
            response.setSuccess(true);
            response.setToken(request.getUsername() + ":" + request.getPassword());
        } else {
            response.setSuccess(false);
            response.setError("Invalid username/password");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("");
    }

}
