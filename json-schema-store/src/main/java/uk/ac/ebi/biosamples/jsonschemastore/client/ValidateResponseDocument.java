package uk.ac.ebi.biosamples.jsonschemastore.client;

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
