package uk.ac.ebi.biosamples.jsonschemastore.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class SchemaBlockDocument {

  //private String id;

  @JsonProperty(value = "$schema")
  private String schema;

  private String title;
  private String description;
  private String type;
  private Map<String, Object> meta;
  private List<String> required;
  private Map<String, Object> properties;

  // a container for all unexpected fields
  private Map<String, Object> schemalessData;

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
