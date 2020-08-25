package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.schema.document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Document(collection = "SchemaBlock")
public class SchemaBlock {
  @TextIndexed(weight = 3F)
  @Id private String id;
  private String schema;
  @TextIndexed(weight = 2F)
  private String title;
  @TextIndexed
  private String description;
  private String type;
  private Map<String, Object> meta;
  private List<String> required;
  private Boolean additionalProperties;
  @TextIndexed(weight = 4F)
  private String jsonSchema;
}
