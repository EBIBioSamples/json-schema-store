package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.util.JsonSchemaMappingUtil;

@Slf4j
@Service
public class MetaSchemaService {

    public JsonNode getMetaSchema() {
        return JsonSchemaMappingUtil.getSchemaObject();
    }
}
