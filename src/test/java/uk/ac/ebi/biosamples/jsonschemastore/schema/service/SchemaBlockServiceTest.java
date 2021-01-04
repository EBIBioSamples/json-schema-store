package uk.ac.ebi.biosamples.jsonschemastore.schema.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.schema.document.SchemaBlock;
import uk.ac.ebi.biosamples.jsonschemastore.schema.repository.SchemaBlockRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SchemaBlockServiceTest {

  @Mock private SchemaBlockRepository schemaBlockRepository;
  @Mock private ModelMapper modelMapper;
  private SchemaBlockService schemaBlockService;

  private SchemaBlock schemaBlock;
  private static final String JSON =
          "{\n  \"$id\": \"https://schemablocks.org/schemas/sb-phenopackets/Disease/v1.0.0\",\n  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n  \"title\": \"Disease\",\n  \"description\": \"Message to indicate a disease (diagnosis) and its recorded onset.\\n\",\n  \"type\": \"object\",\n  \"meta\": {\n    \"provenance\": [\n      {\n        \"description\": \"Phenopackets\",\n        \"id\": \"https://github.com/phenopackets/phenopacket-schema/blob/master/docs/disease.rst\"\n      }\n    ],\n    \"contributors\": [\n      {\n        \"description\": \"GA4GH Data Working Group\"\n      },\n      {\n        \"description\": \"Jules Jacobsen\",\n        \"id\": \"orcid:0000-0002-3265-15918\"\n      },\n      {\n        \"description\": \"Peter Robinson\",\n        \"id\": \"orcid:0000-0002-0736-91998\"\n      },\n      {\n        \"description\": \"Michael Baudis\",\n        \"id\": \"orcid:0000-0002-9903-4248\"\n      },\n      {\n        \"description\": \"Melanie Courtot\",\n        \"id\": \"orcid:0000-0002-9551-6370\"\n      },\n      {\n        \"description\": \"Isuru Liyanage\",\n        \"id\": \"orcid:0000-0002-4839-5158\"\n      }\n    ],\n    \"sb_status\": \"implemented\",\n    \"used_by\": [\n      {\n        \"description\": \"Phenopackets\",\n        \"id\": \"https://github.com/phenopackets/phenopacket-schema/blob/master/docs/disease.rst\"\n      }\n    ]\n  },\n  \"required\": [\n    \"term\"\n  ],\n  \"properties\": {\n    \"ageOfOnset\": {\n      \"allof\": [\n        {\n          \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/Age.json\"\n        },\n        {\n          \"description\": \"The onset of the disease. The values of this will come from the HPO onset hierarchy\\ni.e. subclasses of HP:0003674\\nFHIR mapping: Condition.onset\\n\",\n          \"examples\": [\n            {\n              \"age\": \"P35Y\"\n            }\n          ]\n        }\n      ]\n    },\n    \"term\": {\n      \"allof\": [\n        {\n          \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\"\n        },\n        {\n          \"description\": \"The identifier of this disease\\ne.g. MONDO:0007043, OMIM:101600, Orphanet:710, DOID:14705 (note these are all equivalent)\\n\",\n          \"examples\": [\n            {\n              \"id\": \"MONDO:0007043\"\n            }\n          ]\n        }\n      ]\n    },\n    \"classOfOnset\": {\n      \"description\": \"The onset of the disease. The values of this will come from the HPO onset hierarchy\\ni.e. subclasses of HP:0003674\\nFHIR mapping: Condition.onset\\n\",\n      \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\",\n      \"examples\": [\n        {\n          \"id\": \"HP:0003596\",\n          \"label\": \"Middle age onset\"\n        }\n      ]\n    },\n    \"ageRangeOfOnset\": {\n      \"description\": \"The onset of the disease. The values of this will come from the HPO onset hierarchy\\ni.e. subclasses of HP:0003674\\nFHIR mapping: Condition.onset\\n\",\n      \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/AgeRange.json\",\n      \"examples\": [\n        {\n          \"start\": {\n            \"age\": \"P35Y\"\n          }\n        }\n      ]\n    },\n    \"tnmFinding\": {\n      \"description\": \"Cancer findings in the TNM system that is relevant to the diagnosis of cancer.\\nSee https://www.cancer.gov/about-cancer/diagnosis-staging/staging\\nValid values include child terms of NCIT:C48232 (Cancer TNM Finding)\\n\",\n      \"type\": \"array\",\n      \"items\": {\n        \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\"\n      },\n      \"examples\": [\n        [\n          {\n            \"id\": \"NCIT:C48232\",\n            \"label\": \"Cancer TNM Finding\"\n          }\n        ]\n      ]\n    },\n    \"diseaseStage\": {\n      \"description\": \"Disease staging, the extent to which a disease has developed.\\nFor cancers, see https://www.cancer.gov/about-cancer/diagnosis-staging/staging\\nValid values include child terms of NCIT:C28108 (Disease Stage Qualifier)\\n\",\n      \"type\": \"array\",\n      \"items\": {\n        \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\"\n      },\n      \"examples\": [\n        [\n          {\n            \"id\": \"NCIT:C90529\",\n            \"label\": \"AJCC v6 Stage\"\n          }\n        ]\n      ]\n    }\n  },\n  \"additionalProperties\": false,\n  \"examples\": [\n    {\n      \"term\": {\n        \"id\": \"MONDO:0007043\",\n        \"label\": \"Pfeiffer syndrome\"\n      },\n      \"classOfOnset\": {\n        \"id\": \"HP:0003596\",\n        \"label\": \"Middle age onset\"\n      }\n    }\n  ],\n  \"schemalessData\": {\n    \"oneof\": [\n      {\n        \"properties\": [\n          \"ageOfOnset\"\n        ]\n      },\n      {\n        \"properties\": [\n          \"ageRangeOfOnset\"\n        ]\n      },\n      {\n        \"properties\": [\n          \"classOfOnset\"\n        ]\n      }\n    ]\n  }\n}";


  @BeforeEach
  public void init() {
    schemaBlockRepository = Mockito.mock(SchemaBlockRepository.class);
    modelMapper = new ModelMapper();
    schemaBlockService = new SchemaBlockService(schemaBlockRepository, modelMapper);
    this.schemaBlock = new SchemaBlock();
    schemaBlock.setId("https://schemablocks.org/schemas/sb-phenopackets/Disease/v1.0.0");
    schemaBlock.setJsonSchema(JSON);

    when(schemaBlockRepository.save(any())).thenReturn(schemaBlock);
    when(schemaBlockRepository.insert(Mockito.any(SchemaBlock.class))).thenReturn(schemaBlock);
    when(schemaBlockRepository.findAll()).thenReturn(Collections.singletonList(schemaBlock));
  }

  @Test
  public void testCreateSchemaBlock() {
    SchemaBlockDocument schemaBlockDocument = new SchemaBlockDocument();
    schemaBlockDocument.setTitle("test");
    SchemaBlockDocument result = schemaBlockService.createSchemaBlock(schemaBlockDocument);
    Assertions.assertNotNull(result, "result cannot be null");
  }

  @Test
  public void testCreateSchemaBlockNull() {
    Assertions.assertThrows(
        NullPointerException.class,
        () -> schemaBlockService.createSchemaBlock(null),
        "method argument cannot be null");
  }

  @Test
  public void testGetAllSchemaBlocks() {
    List<SchemaBlockDocument> schemaBlockDocuments = schemaBlockService.getAllSchemaBlocks();
    Assertions.assertFalse(schemaBlockDocuments.isEmpty(), "schemaBlocks cannot be empty");
  }

  @Test
  public void testDeleteSchemaBlocksByIdNull() {
    Assertions.assertThrows(
        NullPointerException.class,
        () -> schemaBlockService.deleteSchemaBlocksById(null),
        "method argument cannot be null");
  }
}
