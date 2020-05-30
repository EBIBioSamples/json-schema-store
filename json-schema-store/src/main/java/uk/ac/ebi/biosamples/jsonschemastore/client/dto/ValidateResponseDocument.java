package uk.ac.ebi.biosamples.jsonschemastore.client.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class ValidateResponseDocument {
  List<String> errors;
  String dataPath;
}
