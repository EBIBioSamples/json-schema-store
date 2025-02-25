package uk.ac.ebi.biosamples.jsonschemastore.ena;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BioSamplesChecklistImporterTest {

  @Autowired
  BioSamplesChecklistImporter bioSamplesChecklistImporter;

  @Test
  public void should_import_three_bsd_checklists() throws Exception {
    assertThat(bioSamplesChecklistImporter.readSchemaFromFile().size()).isEqualTo(3);
  }
}