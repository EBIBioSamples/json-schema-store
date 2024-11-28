package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.biosamples.jsonschemastore.model.LoginRequest;
import uk.ac.ebi.biosamples.jsonschemastore.model.LoginResponse;
import uk.ac.ebi.biosamples.jsonschemastore.model.User;
import uk.ac.ebi.biosamples.jsonschemastore.service.AuthService;

//todo This is just pony auth methods, create proper login functionality

@Slf4j
@RepositoryRestController
@ExposesResourceFor(User.class)
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/me")
    public ResponseEntity<EntityModel<User>> getAuthentication(@CurrentSecurityContext(expression = "authentication")
                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<User> model = EntityModel.of(user);
//        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).getCurrentUser(currentUser)).withSelfRel());
        return ResponseEntity.ok(model);
    }

}
