package uk.ac.ebi.biosamples.jsonschemastore.schema.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.biosamples.jsonschemastore.schema.service.MetaSchemaService;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class MetaSchemaController {
    private MetaSchemaService metaSchemaService;

    public MetaSchemaController(MetaSchemaService metaSchemaService) {
        this.metaSchemaService = metaSchemaService;
    }

    @GetMapping("/metaSchemas")
    public ResponseEntity<JsonNode> getMetaSchema() {
        return ResponseEntity.ok(this.metaSchemaService.getMetaSchema());
    }
}
