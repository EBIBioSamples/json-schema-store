package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Document(collection = "SchemaBlock")
public class SchemaBlock {
  @Id private String id;
  private String schema;
  private String title;
  private String description;
  private String type;
  private Map<String, Object> meta;
  private List<String> required;
  private Boolean additionalProperties;
  private String jsonSchema;
}
