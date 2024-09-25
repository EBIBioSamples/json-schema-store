package uk.ac.ebi.biosamples.jsonschemastore.service;

public class VersionIncrementer {
    public static String incrementMinorVersion(String version) {
        String[] versionComponents = version.split("\\.");
        String buildNumber = versionComponents[1];
        versionComponents[1] = String.valueOf(Integer.valueOf(buildNumber) + 1);
        return String.join(".", versionComponents);
    }
}
