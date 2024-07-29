package uk.ac.ebi.biosamples.jsonschemastore.model;

import java.util.List;

public record ValidationError(String dataPath, List<String> errors) {
}
