package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.config.SchemaStoreProperties;
import uk.ac.ebi.biosamples.jsonschemastore.ena.ChecklistGroupResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistGroupService {

    private final SchemaStoreProperties schemaStoreProperties;
    public List<ChecklistGroupResponse> fetchReport(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ChecklistGroupResponse[] reports = restTemplate.getForObject(url, ChecklistGroupResponse[].class);
        return List.of(reports);
    }

    public String findGroupForAccession(String accession) {
        return fetchReport(schemaStoreProperties.getEnaChecklistGroupsUrl())
                .stream()
                .map(ChecklistGroupResponse::getReport)
                .filter(r->r.getChecklist().contains(accession))
                .findFirst()
                .map(r->r.getName())
                .orElse("no ENA group found");
    }
}
