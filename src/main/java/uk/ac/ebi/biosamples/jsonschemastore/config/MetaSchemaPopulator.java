package uk.ac.ebi.biosamples.jsonschemastore.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;
import uk.ac.ebi.biosamples.jsonschemastore.repository.MetaSchemaRepository;

public class MetaSchemaPopulator extends Jackson2RepositoryPopulatorFactoryBean {
//    @Value("classpath:indicatorData.json")
//    private String metaSchemaFile;
//
//    @Autowired
//    private MetaSchemaRepository metaSchemaRepository;
//
//    @Bean
//    @Autowired
//    public void repositoryPopulator(ObjectMapper objectMapper) {
//
//    }
//}
//
//    {
//        val factory = Jackson2RepositoryPopulatorFactoryBean()
//        indicatorRepository.deleteAll()
//        factory.setMapper(objectMapper)
//        factory.setResources(arrayOf(data))
//        return factory
//    }
            }
