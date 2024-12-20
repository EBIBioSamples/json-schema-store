package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document
//@SuperBuilder
@NoArgsConstructor
public class FieldGroup {
  @Id
  private String id;
  private String name;
  private String description;
  private Set<String> fields;
  @CreatedDate
  private LocalDateTime createdDate;
  @LastModifiedDate
  private LocalDateTime lastModifiedDate;

  public FieldGroup(String name) {
    this.name = name;
    fields = new HashSet<>();
  }
}
