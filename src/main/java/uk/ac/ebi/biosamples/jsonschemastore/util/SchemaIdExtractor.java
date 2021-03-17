package uk.ac.ebi.biosamples.jsonschemastore.util;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Component
public class SchemaIdExtractor {

    public static String getVersion(String id) {
        String schemaId = Objects.requireNonNull(id, "$id filed cannot be null!");
        String[] pathSegments = decomposeSchemaId(schemaId);
        String schemaVersion = "1.0.0";
        if (pathSegments.length > 1) {
            schemaVersion = pathSegments[pathSegments.length - 1];
        }

        return schemaVersion;
    }

    public static String getSchemaName(String id) {
        String schemaId = Objects.requireNonNull(id, "$id filed cannot be null!");
        String[] pathSegments = decomposeSchemaId(schemaId);
        String schemaName;
        if (pathSegments.length > 2) {
            schemaName = pathSegments[pathSegments.length - 2];
        } else {
            schemaName = pathSegments[0];
        }

        return schemaName;
    }

    public static String[] decomposeSchemaId(String schemaId) {
        String path;
        try {
            path = new URL(schemaId).getPath();
        } catch (MalformedURLException e) {
            path = schemaId;
        }
        return path.split("/");
    }
}
