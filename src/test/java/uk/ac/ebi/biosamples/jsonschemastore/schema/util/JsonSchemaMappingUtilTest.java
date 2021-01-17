package uk.ac.ebi.biosamples.jsonschemastore.schema.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.biosamples.jsonschemastore.dto.SchemaBlockDocument;
import uk.ac.ebi.biosamples.jsonschemastore.document.SchemaBlock;

class JsonSchemaMappingUtilTest {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final String JSON =
      "{\n  \"$id\": \"https://schemablocks.org/schemas/sb-phenopackets/Disease/v1.0.0\",\n  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n  \"title\": \"Disease\",\n  \"description\": \"Message to indicate a disease (diagnosis) and its recorded onset.\\n\",\n  \"type\": \"object\",\n  \"meta\": {\n    \"provenance\": [\n      {\n        \"description\": \"Phenopackets\",\n        \"id\": \"https://github.com/phenopackets/phenopacket-schema/blob/master/docs/disease.rst\"\n      }\n    ],\n    \"contributors\": [\n      {\n        \"description\": \"GA4GH Data Working Group\"\n      },\n      {\n        \"description\": \"Jules Jacobsen\",\n        \"id\": \"orcid:0000-0002-3265-15918\"\n      },\n      {\n        \"description\": \"Peter Robinson\",\n        \"id\": \"orcid:0000-0002-0736-91998\"\n      },\n      {\n        \"description\": \"Michael Baudis\",\n        \"id\": \"orcid:0000-0002-9903-4248\"\n      },\n      {\n        \"description\": \"Melanie Courtot\",\n        \"id\": \"orcid:0000-0002-9551-6370\"\n      },\n      {\n        \"description\": \"Isuru Liyanage\",\n        \"id\": \"orcid:0000-0002-4839-5158\"\n      }\n    ],\n    \"sb_status\": \"implemented\",\n    \"used_by\": [\n      {\n        \"description\": \"Phenopackets\",\n        \"id\": \"https://github.com/phenopackets/phenopacket-schema/blob/master/docs/disease.rst\"\n      }\n    ]\n  },\n  \"required\": [\n    \"term\"\n  ],\n  \"properties\": {\n    \"ageOfOnset\": {\n      \"allof\": [\n        {\n          \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/Age.json\"\n        },\n        {\n          \"description\": \"The onset of the disease. The values of this will come from the HPO onset hierarchy\\ni.e. subclasses of HP:0003674\\nFHIR mapping: Condition.onset\\n\",\n          \"examples\": [\n            {\n              \"age\": \"P35Y\"\n            }\n          ]\n        }\n      ]\n    },\n    \"term\": {\n      \"allof\": [\n        {\n          \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\"\n        },\n        {\n          \"description\": \"The identifier of this disease\\ne.g. MONDO:0007043, OMIM:101600, Orphanet:710, DOID:14705 (note these are all equivalent)\\n\",\n          \"examples\": [\n            {\n              \"id\": \"MONDO:0007043\"\n            }\n          ]\n        }\n      ]\n    },\n    \"classOfOnset\": {\n      \"description\": \"The onset of the disease. The values of this will come from the HPO onset hierarchy\\ni.e. subclasses of HP:0003674\\nFHIR mapping: Condition.onset\\n\",\n      \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\",\n      \"examples\": [\n        {\n          \"id\": \"HP:0003596\",\n          \"label\": \"Middle age onset\"\n        }\n      ]\n    },\n    \"ageRangeOfOnset\": {\n      \"description\": \"The onset of the disease. The values of this will come from the HPO onset hierarchy\\ni.e. subclasses of HP:0003674\\nFHIR mapping: Condition.onset\\n\",\n      \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/AgeRange.json\",\n      \"examples\": [\n        {\n          \"start\": {\n            \"age\": \"P35Y\"\n          }\n        }\n      ]\n    },\n    \"tnmFinding\": {\n      \"description\": \"Cancer findings in the TNM system that is relevant to the diagnosis of cancer.\\nSee https://www.cancer.gov/about-cancer/diagnosis-staging/staging\\nValid values include child terms of NCIT:C48232 (Cancer TNM Finding)\\n\",\n      \"type\": \"array\",\n      \"items\": {\n        \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\"\n      },\n      \"examples\": [\n        [\n          {\n            \"id\": \"NCIT:C48232\",\n            \"label\": \"Cancer TNM Finding\"\n          }\n        ]\n      ]\n    },\n    \"diseaseStage\": {\n      \"description\": \"Disease staging, the extent to which a disease has developed.\\nFor cancers, see https://www.cancer.gov/about-cancer/diagnosis-staging/staging\\nValid values include child terms of NCIT:C28108 (Disease Stage Qualifier)\\n\",\n      \"type\": \"array\",\n      \"items\": {\n        \"$ref\": \"https://schemablocks.org/schemas/sb-phenopackets/v1.0.0/OntologyClass.json\"\n      },\n      \"examples\": [\n        [\n          {\n            \"id\": \"NCIT:C90529\",\n            \"label\": \"AJCC v6 Stage\"\n          }\n        ]\n      ]\n    }\n  },\n  \"additionalProperties\": false,\n  \"examples\": [\n    {\n      \"term\": {\n        \"id\": \"MONDO:0007043\",\n        \"label\": \"Pfeiffer syndrome\"\n      },\n      \"classOfOnset\": {\n        \"id\": \"HP:0003596\",\n        \"label\": \"Middle age onset\"\n      }\n    }\n  ],\n  \"schemalessData\": {\n    \"oneof\": [\n      {\n        \"properties\": [\n          \"ageOfOnset\"\n        ]\n      },\n      {\n        \"properties\": [\n          \"ageRangeOfOnset\"\n        ]\n      },\n      {\n        \"properties\": [\n          \"classOfOnset\"\n        ]\n      }\n    ]\n  }\n}";

  @Test
  public void testConvertSchemaBlockToJson() throws JsonProcessingException {
    SchemaBlock schemaBlock = new SchemaBlock();
    schemaBlock.setDescription("test description");
    schemaBlock.setTitle("schema title");
    JsonNode jsonNode = JsonSchemaMappingUtil.convertSchemaBlockToJson(schemaBlock);

    Assertions.assertNotNull(jsonNode, "jsonNode cannot be null.");
    Assertions.assertTrue(jsonNode.has("description"));
    Assertions.assertEquals(
        "test description",
        jsonNode.get("description").asText(),
        "description value did not match");
    Assertions.assertTrue(jsonNode.has("title"));
    Assertions.assertEquals(
        "schema title", jsonNode.get("title").asText(), "title value did not match.");
  }

  @Test
  public void testConvertObjectToJson() throws JsonProcessingException {
    SchemaBlockDocument schemaBlockDocument = objectMapper.readValue(JSON, SchemaBlockDocument.class);
    JsonNode jsonNode = JsonSchemaMappingUtil.convertObjectToJson(schemaBlockDocument);

    Assertions.assertNotNull(jsonNode, "jsonNode cannot be null.");
    Assertions.assertTrue(jsonNode.has("description"));
    Assertions.assertEquals(
        schemaBlockDocument.getDescription(),
        jsonNode.get("description").asText(),
        "description value did not match");
    Assertions.assertTrue(jsonNode.has("title"));
    Assertions.assertEquals(
            schemaBlockDocument.getTitle(), jsonNode.get("title").asText(), "title value did not match.");
  }
}
