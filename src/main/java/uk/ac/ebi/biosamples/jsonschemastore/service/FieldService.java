package uk.ac.ebi.biosamples.jsonschemastore.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import uk.ac.ebi.biosamples.jsonschemastore.model.Field;
import uk.ac.ebi.biosamples.jsonschemastore.model.FieldId;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@AllArgsConstructor
@Component
@Slf4j
public class FieldService {
    private final SchemaService schemaService;
    private final FieldRepository fieldRepository;
    private final UserService userService;
    @Qualifier("fieldServiceObjectMapper")
    private final ObjectMapper objectMapper;

    public void initNewField(Field field) {
        initNewField(field, "1.0");
    }

    public Field safeGetFieldById(Field field) {
        return fieldRepository.findById(field.getId())
                .orElseThrow(() -> new DataIntegrityViolationException("Could not find the field: " + field.getId()));
    }

    public void initNewField(Field field, String version) {
        field.setVersion(version);
        field.setName(toVariableName(field.getLabel()));
        field.setId(new FieldId(field.getName(), field.getVersion()).asString());
        LocalDateTime now = LocalDateTime.now();
        field.setCreatedDate(now);
        field.setLastModifiedDate(now);
        String username = userService.findCurrentUser().getUsername();
        field.setCreatedBy(username);
        field.setLastModifiedBy(username);
        field.markLatest();
    }

    public boolean isUpdateAllowed(Field field) {
        return field.isLatest();
    }

    boolean isLabelChange(Field field, Field oldField) {
        return !oldField.getLabel().equals(field.getLabel());
    }

    Field cloneField(Field field) {
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Skip `null` values globally
            objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(objectMapper.writeValueAsString(field), Field.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("problem cloning original field: " + field.getId() + " " + e.getMessage(), e);
        }
    }

    boolean isGroupOnlyModification(Field oldField, Field modifiedField) {
        Field oldFieldCopy = cloneField(oldField);
        oldFieldCopy.setGroup(modifiedField.getGroup());
        return oldFieldCopy.equals(modifiedField);
    }

    public void updateUsedBySchemas(Field field, String oldFieldId) {
        Set<String> schemas = field.getUsedBySchemas();
        Set<String> updatedSchemaIds = schemas.stream()
                .map((String id) -> id.split(":"))
                .collect(Collectors.toMap(
                        arr -> arr[0], // accession part
                        arr -> arr[1], //  version part
                        (v1, v2) -> compareVersions(v1, v2) > 0 ? v1 : v2
                ))
                .entrySet().stream()
                .map(e->e.getKey()+":"+e.getValue())
                .<String>map((String schemaId) -> schemaService.updateSchemaFieldAssociationAndIncrementVersion(field, oldFieldId, schemaId))
                .collect(Collectors.toSet());
        field.setUsedBySchemas(updatedSchemaIds);
        fieldRepository.save(field);
    }

    private static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }

    public Field save(Field field) {
        return fieldRepository.save(field);
    }

    public List<Field> saveAll(Iterable<Field> fields) {
        return fieldRepository.saveAll(fields);
    }

    public Page<Field> findByExample(Field field, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("usedBySchemas")
                .withMatcher("searchIndex", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<Field> example = Example.of(field, matcher);

        return fieldRepository.findAll(example, pageable);
    }
}
