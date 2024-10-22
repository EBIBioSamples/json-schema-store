package uk.ac.ebi.biosamples.jsonschemastore.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.biosamples.jsonschemastore.ena.ChecklistGroupResponse;

import java.util.List;

@Service
public class ChecklistGroupService {


    public List<ChecklistGroupResponse> fetchReport(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ChecklistGroupResponse[] reports = restTemplate.getForObject(url, ChecklistGroupResponse[].class);
        return List.of(reports);
    }

    public String findGroupForAccession(String accession) {
        return fetchReport("https://wwwdev.ebi.ac.uk/ena/dev/submit/report/checklist-groups")
                .stream()
                .map(ChecklistGroupResponse::getReport)
                .filter(r->r.getChecklist().contains(accession))
                .findFirst()
                .map(r->r.getName())
                .orElse("no ENA group found");
    }
}
