package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.ac.ebi.biosamples.jsonschemastore.service.VersionIncrementer;

@Data
@AllArgsConstructor
public class SchemaId {
    private String accession;
    private String version;

    public String asString() {
        return accession+":"+version;
    }

    public static SchemaId incrementMinorVersion(SchemaId schemaId) {
        return new SchemaId(schemaId.accession, VersionIncrementer.incrementMinorVersion(schemaId.version));
    }

    public static SchemaId fromString(String id) {
        String[] idComponents = id.split(":");
        if(idComponents.length!=2) {
            throw new IllegalArgumentException("expecting id to be in accession:version format. Received: " + id);
        }
        return new SchemaId(idComponents[0], idComponents[1]);
    }


}
