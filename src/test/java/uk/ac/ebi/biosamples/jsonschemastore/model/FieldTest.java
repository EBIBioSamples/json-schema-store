package uk.ac.ebi.biosamples.jsonschemastore.model;

import org.junit.jupiter.api.Test;
import uk.ac.ebi.biosamples.jsonschemastore.FieldGeneratorHelper;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class FieldTest {

  @Test
  void fromPropertyShouldPopulateUnitsCorrectlyWhenAvailable() {
    String unit = "m";
    Property property = FieldGeneratorHelper.getTestPropertyWithUnits(Collections.singletonList(unit));
    Field expectedField = Field.fromProperty(property);
    assertThat(expectedField.units.contains(unit)).isTrue();
  }

  @Test
  void fromPropertyShouldNotThrowErrorWhenUnitsIsEmpty() {
    Property property = FieldGeneratorHelper.getTestPropertyWithUnits(Collections.emptyList());
    Field expectedField = Field.fromProperty(property);
    assertThat(expectedField.units.isEmpty()).isTrue();
  }

  @Test
  void fromPropertyShouldNotThrowErrorWhenUnitsIsNull() {
    Property property = FieldGeneratorHelper.getTestPropertyWithUnits(null);
    Field expectedField = Field.fromProperty(property);
    assertThat(expectedField.units.isEmpty()).isTrue();
  }
}