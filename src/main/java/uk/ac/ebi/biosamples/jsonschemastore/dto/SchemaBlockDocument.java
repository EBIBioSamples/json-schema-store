package uk.ac.ebi.biosamples.jsonschemastore.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonSerialize(using = SchemaBlockDocumentSerializer.class)
public class SchemaBlockDocument {
  @NonNull
  private String id;
  private String schemaName;
  private String version;
  private String schema;
  private String title;
  private String description;
  private String type;
  private Map<String, Object> meta;
  private List<String> required;
  private Boolean additionalProperties;
  @NonNull
  private String jsonSchema;
}
