package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@Slf4j
public class RootRedirectController {

    @Value("${spring.data.rest.basePath}")
    private String apiRootResource;

    @Value("${server.servlet.contextPath:}")
    private String contextPath;

    @GetMapping("/")
    public RedirectView redirectToApiRoot() {
        log.info("context url {}", ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
        return new RedirectView(contextPath + apiRootResource);
    }
}
