package uk.ac.ebi.biosamples.jsonschemastore.model;

import java.util.List;

public record Property(String name,
                       List<String> synonyms,
                       String description,
                       String type,
                       List<String> units,
                       RequirementType requirementType,
                       Multiplicity multiplicity
                       ) {
  public enum RequirementType {
    MANDATORY, RECOMMENDED, OPTIONAL
  }

  public enum Multiplicity {
    SINGLE, MULTIPLE
  }
}
