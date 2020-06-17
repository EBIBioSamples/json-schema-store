package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class MetaSchemaBlock {
  private Object metaSchema;
}
