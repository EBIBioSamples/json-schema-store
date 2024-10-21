package uk.ac.ebi.biosamples.jsonschemastore.ena;

import lombok.Data;

import java.util.List;

@Data
public class ChecklistGroupResponse {
    private ChecklistGroupReport report;
    private List<Object> links;
}
