package uk.ac.ebi.biosamples.jsonschemastore.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import uk.ac.ebi.biosamples.jsonschemastore.model.JsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.MetaSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaOutline;

import java.io.IOException;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
    /*ObjectMapper jsonMapper = new ObjectMapper();
    modelMapper.typeMap(JsonSchema.class, MongoJsonSchema.class).addMappings(mapper -> {
      mapper.map(src -> src.getSchema().toString(), MongoJsonSchema::setSchema);
    });

    modelMapper.typeMap(MongoJsonSchema.class, JsonSchema.class).addMappings(mapper -> {
      mapper.map(src -> {
        try {
          return jsonMapper.readTree(src.getSchema());
        } catch (JsonProcessingException e) {
          log.error("Couldn't convert mongo model to JSON: {}", e.getMessage());
          return null;
        }
      }, JsonSchema::setSchema);
    });*/
        return modelMapper;
    }

    // testing on the localhost
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

    // initially loading basic schemas/metaschemas into the db
    @Bean
    public Jackson2RepositoryPopulatorFactoryBean getRespositoryPopulator() {
        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
        factory.setResources(new Resource[]{new ClassPathResource("env/init_db_schema.json")});
        return factory;
    }
}
