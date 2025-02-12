package uk.ac.ebi.biosamples.jsonschemastore.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RootRedirectController {

    @Value("${spring.data.rest.basePath}")
    private String apiRootResource;

    @GetMapping("/")
    public RedirectView redirectToTarget() {
        return new RedirectView(apiRootResource);
    }
}
