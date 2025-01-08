package uk.ac.ebi.biosamples.jsonschemastore.config;

import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldGroup;

@Component
public class ExposeEntityIdRestMvcConfiguration implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
    config.exposeIdsFor(FieldGroup.class);
  }
}