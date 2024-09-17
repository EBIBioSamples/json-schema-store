package uk.ac.ebi.biosamples.jsonschemastore.model;

import java.util.List;

public record Property(String name, List<String> synonyms, String description, String type, List<String> units,
                       AttributeCardinality cardinality) {
  public enum AttributeCardinality {
    MANDATORY, RECOMMENDED, OPTIONAL
  }
}
