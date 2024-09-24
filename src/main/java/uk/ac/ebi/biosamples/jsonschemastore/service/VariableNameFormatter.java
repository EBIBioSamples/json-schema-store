package uk.ac.ebi.biosamples.jsonschemastore.service;

// TODO: make a Srping Componet rather than static
public class VariableNameFormatter {
    public static String toVariableName(String name) {
        // Replace spaces with underscores
        String formatted = name.replaceAll("\\s+", "_");
        // Convert to lowercase
        formatted = formatted.toLowerCase();

        // Remove any non-alphanumeric characters except underscores
        formatted = formatted.replaceAll("[^a-z0-9_]", "");

        // Ensure the name doesn't start with a digit
        if (Character.isDigit(formatted.charAt(0))) {
            formatted = "_" + formatted;
        }
        return formatted;
    }
}
