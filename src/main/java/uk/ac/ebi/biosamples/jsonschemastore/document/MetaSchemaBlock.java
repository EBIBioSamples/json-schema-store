package uk.ac.ebi.biosamples.jsonschemastore.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class MetaSchemaBlock {
  private Object metaSchema;
}
