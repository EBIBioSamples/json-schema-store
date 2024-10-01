package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.ac.ebi.biosamples.jsonschemastore.service.VersionIncrementer;

@Data
@AllArgsConstructor
public class FieldId {
    private String name;
    private String version;

    public String asString() {
        return name +":"+version;
    }

    public static FieldId incrementMinorVersion(FieldId schemaId) {
        return new FieldId(schemaId.name, VersionIncrementer.incrementMinorVersion(schemaId.version));
    }

    public static FieldId fromString(String id) {
        String[] idComponents = id.split(":");
        if(idComponents.length!=2) {
            throw new IllegalArgumentException("expecting id to be in name:version format. Received: " + id);
        }
        return new FieldId(idComponents[0], idComponents[1]);
    }


}
