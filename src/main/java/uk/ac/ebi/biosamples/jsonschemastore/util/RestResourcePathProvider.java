package uk.ac.ebi.biosamples.jsonschemastore.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMetadata;
import org.springframework.data.rest.core.mapping.RepositoryResourceMappings;
import org.springframework.stereotype.Component;

@Component
public class RestResourcePathProvider {

    private final RepositoryRestConfiguration restConfiguration;
    private final RepositoryResourceMappings resourceMappings;

    @Autowired
    public RestResourcePathProvider(RepositoryRestConfiguration restConfiguration,
                                    RepositoryResourceMappings resourceMappings) {
        this.restConfiguration = restConfiguration;
        this.resourceMappings = resourceMappings;
    }

    public String getResourcePath(Class<?> domainType) {
        // Get resource metadata for the domain type (entity class)
        ResourceMetadata metadata = resourceMappings.getMetadataFor(domainType);
        if (metadata != null) {
            // Get the path relative to the base path
            String resourcePath = metadata.getPath().toString();
            // Combine base path and resource path
            return restConfiguration.getBasePath().getPath() + "/" + resourcePath;
        }
        return null;
    }
}

