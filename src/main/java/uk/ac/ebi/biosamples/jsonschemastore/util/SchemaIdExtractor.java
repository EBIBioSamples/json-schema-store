package uk.ac.ebi.biosamples.jsonschemastore.util;

import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.exception.MalformedSchemaException;
import uk.ac.ebi.biosamples.jsonschemastore.model.SchemaId;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

@Component
public class SchemaIdExtractor {

    public static SchemaId validateSchemaId(String id) {
        String schemaId = Objects.requireNonNull(id, "$id filed cannot be null!");
        String[] pathSegments = decomposeSchemaId(schemaId);
        String domain = String.join("/", Arrays.asList(Arrays.copyOfRange(pathSegments, 0, pathSegments.length - 2)));
        String name = pathSegments[pathSegments.length - 2];
        String version = pathSegments[pathSegments.length - 1];

        validateVersion(version);
        return new SchemaId(schemaId, domain, name, version);
    }

    public static SchemaId incrementSchemaId(String id) {
        SchemaId schemaId = validateSchemaId(id);
        String[] versionSegments = schemaId.getVersion().split("\\.");
        versionSegments[2] = String.valueOf(Integer.parseInt(versionSegments[2]) + 1);
        String version = String.join(".", Arrays.asList(versionSegments));
        String newId = String.join("/", schemaId.getDomain(), schemaId.getName(), version);

        return new SchemaId(newId, schemaId.getDomain(), schemaId.getName(), version);
    }

    public static String[] decomposeSchemaId(String schemaId) {
        String[] pathSegments = schemaId.split("/");
        if (pathSegments.length < 3) {
            throw new MalformedSchemaException("Schema id should consist of domain/schema_name/version");
        }

        return pathSegments;
    }

    public static void validateVersion(String version) {
        String[] versionSegments = version.split("\\.");
        if (versionSegments.length != 3) {
            throw new MalformedSchemaException("Version should consist of major.minor.patch");
        }
    }
}
