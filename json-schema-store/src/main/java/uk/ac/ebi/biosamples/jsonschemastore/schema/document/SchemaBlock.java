package uk.ac.ebi.biosamples.jsonschemastore.schema.document;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Document(collection = "SchemaBlock")
public class SchemaBlock {
  @Id
  private String id;
  private String schema;
  private String title;
  private String description;
  private String type;
  private Map<String, Object> meta;
  private List<String> required;
  private Map<String, Object> properties;

  // a container for all unexpected fields
  private Map<String, Object> schemalessData;

  public SchemaBlock() {
    super();
  }

  @JsonAnySetter
  public void add(String key, Object value) {
    if (null == schemalessData) {
      schemalessData = new HashMap<>();
    }
    schemalessData.put(key, value);
  }

  @JsonAnyGetter
  public Map<String, Object> get() {
    return schemalessData;
  }
}
