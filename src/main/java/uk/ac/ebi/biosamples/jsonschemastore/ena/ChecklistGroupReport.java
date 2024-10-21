package uk.ac.ebi.biosamples.jsonschemastore.ena;

import lombok.Data;
        import java.util.List;

@Data
public class ChecklistGroupReport {
    private String name;
    private String description;
    private List<String> checklist;
}

