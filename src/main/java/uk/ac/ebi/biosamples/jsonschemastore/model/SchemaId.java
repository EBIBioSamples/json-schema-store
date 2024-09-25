package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchemaId {
    private String accession;
    private String version;

    public String asString() {
        return accession+":"+version;
    }

    public static SchemaId incrementMinorVersion(SchemaId schemaId) {
        String[] versionComponents = schemaId.version.split("\\.");
        if(versionComponents.length!=2) {
            throw new IllegalArgumentException("expecting version to be major.minor format: " + schemaId.version);
        }
        String minorVersion = versionComponents[1];
        versionComponents[1] = String.valueOf(Integer.valueOf(minorVersion) + 1);
        String incrementdVersion = String.join(".", versionComponents);
        return new SchemaId(schemaId.accession, incrementdVersion);
    }

    public static SchemaId fromString(String id) {
        String[] idComponents = id.split(":");
        if(idComponents.length!=2) {
            throw new IllegalArgumentException("expecting id to be in accession:version format. Received: " + id);
        }
        return new SchemaId(idComponents[0], idComponents[1]);
    }


}
