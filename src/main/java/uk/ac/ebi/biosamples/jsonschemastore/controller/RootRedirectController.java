package uk.ac.ebi.biosamples.jsonschemastore.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@Slf4j
public class RootRedirectController {

    @Value("${spring.data.rest.basePath}")
    private String apiRootResource;

    @Value("${server.servlet.contextPath:}")
    private String contextPath;

    @GetMapping("/")
    public RedirectView redirectToApiRoot(@RequestHeader Map<String, String> headers) {
        headers.forEach((key, value) -> log.info("header {}: {}", key, value));
        return new RedirectView(contextPath + apiRootResource);
    }
}
