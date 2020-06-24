package uk.ac.ebi.biosamples.jsonschema.jsonschemastore.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = SchemaBlockDocumentDeserializer.class)
public class SchemaBlockDocument {
  private String id;
  private String schema;
  private String title;
  private String description;
  private String type;
  private Map<String, Object> meta;
  private List<String> required;
  private Boolean additionalProperties;
  private String jsonSchema;
}
