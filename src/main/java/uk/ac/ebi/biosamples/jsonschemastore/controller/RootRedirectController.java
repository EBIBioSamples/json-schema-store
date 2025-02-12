package uk.ac.ebi.biosamples.jsonschemastore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.RedirectUrlBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RootRedirectController {

    @Value("${spring.data.rest.basePath}")
    private String apiRootResource;

    @Value("${server.servlet.contextPath:}")
    private String contextPath;

    @GetMapping("/")
    public RedirectView redirectToTarget() {
        return new RedirectView(contextPath + apiRootResource);
    }
}
