package uk.ac.ebi.biosamples.jsonschemastore.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldGroup;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldGroupRepository;

@RequiredArgsConstructor
@Slf4j
@Component
public class FieldGroupService {
    private final FieldGroupRepository fieldGroupRepository;

    public void updateGroups(String fieldLabel, String oldGroupId, String newGroupId) {
        log.info("updating group of field {} from {} to {}", fieldLabel, oldGroupId, newGroupId);
        if (oldGroupId != null && oldGroupId.equals(newGroupId)) {
            return;
        }

        removeFieldFromGroup(fieldLabel, oldGroupId);
        addFieldToGroup(fieldLabel, newGroupId);
    }

    private void removeFieldFromGroup(String fieldName, String oldGroupId) {
        FieldGroup oldGroup = fieldGroupRepository.findById(oldGroupId)
                .orElseThrow(() -> new DataIntegrityViolationException("Invalid group id: " + oldGroupId));
        oldGroup.getFields().remove(fieldName);
        fieldGroupRepository.save(oldGroup);
    }

    private void addFieldToGroup(String fieldName, String newGroupId) {
        FieldGroup newGroup = fieldGroupRepository.findById(newGroupId)
                .orElseThrow(() -> new DataIntegrityViolationException("Invalid group id: " + newGroupId));
        newGroup.getFields().add(fieldName);
        fieldGroupRepository.save(newGroup);
    }
}
