package uk.ac.ebi.biosamples.jsonschemastore;


import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.Property;

import java.util.Collections;
import java.util.List;

@Slf4j
public final class FieldGeneratorHelper {

  private FieldGeneratorHelper() {
  }

  public static Property getTestPropertyWithUnits(List<String> units) {
    String propertyType = """
        {
          "type": "string",
          "pattern": "((0|((0\\\\.)|([1-9][0-9]*\\\\.?))[0-9]*)([Ee][+-]?[0-9]+)?)|((^not collected$)|(^not provided$)|(^restricted access$)|(^missing: control sample$)|(^missing: sample group$)|(^missing: synthetic construct$)|(^missing: lab stock$)|(^missing: third party data$)|(^missing: data agreement established pre-2023$)|(^missing: endangered species$)|(^missing: human-identifiable$))"
        }
        """;
    return new Property("altitude", Collections.emptyList(),
        "vertical distance from Earth's surface", propertyType, units,
        Property.RequirementType.MANDATORY, Property.Multiplicity.SINGLE, "Collection event information");
  }

  public static Field getTestFieldWithIdAndUnits(String id, List<String> units) {
    Field field = Field.fromProperty(getTestPropertyWithUnits(units));
    field.setId(id);
    return field;
  }
}
