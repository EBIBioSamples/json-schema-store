package uk.ac.ebi.biosamples.jsonschemastore.schema.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "SchemaBlock")
public class SchemaBlock {
  SchemaBlock() {
    super();
  }
  @Id private Integer id;
  private String name;
  private Object schema;
}
