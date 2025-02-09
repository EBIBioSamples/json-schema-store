package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.ac.ebi.biosamples.jsonschemastore.service.VersionIncrementer;

@Data
@AllArgsConstructor
public class SchemaId {
    public static final String DELIMITER = ":";
    private String accession;
    private String version;

    public static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }

    public static String[] split(String id) {
        return id.split(DELIMITER);
    }

    public String asString() {
        return accession+ DELIMITER +version;
    }

    public static SchemaId incrementMinorVersion(SchemaId schemaId) {
        return new SchemaId(schemaId.accession, VersionIncrementer.incrementMinorVersion(schemaId.version));
    }

    public static SchemaId fromString(String id) {
        String[] idComponents = id.split(DELIMITER);
        if(idComponents.length!=2) {
            throw new IllegalArgumentException("expecting id to be in accession:version format. Received: " + id);
        }
        return new SchemaId(idComponents[0], idComponents[1]);
    }


}
