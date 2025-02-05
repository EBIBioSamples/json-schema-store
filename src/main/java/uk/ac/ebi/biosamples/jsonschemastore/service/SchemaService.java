package uk.ac.ebi.biosamples.jsonschemastore.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import uk.ac.ebi.biosamples.jsonschemastore.model.*;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.MongoJsonSchema;
import uk.ac.ebi.biosamples.jsonschemastore.model.mongo.SchemaFieldAssociation;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldGroupRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.FieldRepository;
import uk.ac.ebi.biosamples.jsonschemastore.repository.SchemaRepository;
import uk.ac.ebi.biosamples.jsonschemastore.util.MongoModelConverter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaService {
    private final AccessioningService accessioningService;
    private final SchemaRepository schemaRepository;
    private final FieldRepository fieldRepository;
    private final MongoModelConverter modelConverter;
    private final FieldGroupRepository fieldGroupRepository;

    public Optional<JsonSchema> getSchemaById(@NonNull String id) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findById(id);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Optional<JsonSchema> getSchemaByIdOrLatestByAccession(@NonNull String idOrAccession) {
        return idOrAccession.contains(":") ? getSchemaById(idOrAccession) : getLatestSchemaByAccession(idOrAccession);
    }

    public Optional<JsonSchema> getLatestSchemaByAccession(@NonNull String accession) {
        return schemaRepository.findFirstByAccessionOrderByVersionDesc(accession)
                .map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Optional<JsonSchema> getSchemaByAccessionAndVersion(@NonNull String accession, String version) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findFirstByAccessionAndVersionOrderByVersionDesc(accession, version);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Page<SchemaOutline> getAllVersionsByAccession(@NonNull String accession, int page, int size) {
        Page<MongoJsonSchema> mongoSchemas = schemaRepository.findByAccessionOrderByVersionDesc(accession, PageRequest.of(page, size));

        List<SchemaOutline> schemas = mongoSchemas.stream()
                .map(modelConverter::mongoJsonSchemaToSchemaOutline)
                .collect(Collectors.toList());
        return new PageImpl<>(schemas, PageRequest.of(page, size), mongoSchemas.getTotalElements());
    }

    public Optional<JsonSchema> getSchemaByNameAndVersion(@NonNull String schemaName, String version) {
        Optional<MongoJsonSchema> optionalSchema = schemaRepository.findFirstByNameAndVersionOrderByVersionDesc(schemaName, version);
        return optionalSchema.map(modelConverter::mongoJsonSchemaToJsonSchema);
    }

    public Page<JsonSchema> getSchemaPage(String text, int page, int size) {
        Page<MongoJsonSchema> mongoSchemas;
        if (text.isEmpty()) {
            mongoSchemas = schemaRepository.findAll(PageRequest.of(page, size));
        } else {
            TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matchingAny(text);
            mongoSchemas = schemaRepository.findAllBy(textCriteria, PageRequest.of(page, size));
        }

        List<JsonSchema> schemas = mongoSchemas.stream()
                .map(modelConverter::mongoJsonSchemaToJsonSchema)
                .collect(Collectors.toList());
        return new PageImpl<>(schemas, PageRequest.of(page, size), mongoSchemas.getTotalElements());
    }

    public Page<SchemaOutline> getSchemaList(int page, int size) {
        Page<MongoJsonSchema> mongoSchemas = schemaRepository.findAll(PageRequest.of(page, size));

        List<SchemaOutline> schemas = mongoSchemas.stream()
                .map(modelConverter::mongoJsonSchemaToSchemaOutline)
                .collect(Collectors.toList());
        return new PageImpl<>(schemas, PageRequest.of(page, size), mongoSchemas.getTotalElements());
    }

    public JsonSchema saveSchema(@NonNull JsonSchema jsonSchema) {
        String accession = jsonSchema.getAccession();
        if (accession == null || accession.isEmpty()) {
            populateAccession(jsonSchema);
        }

        MongoJsonSchema mongoJsonSchema = modelConverter.jsonSchemaToMongoJsonSchema(jsonSchema);
        MongoJsonSchema mongoJsonSchemaResult = schemaRepository.save(mongoJsonSchema);
        return modelConverter.mongoJsonSchemaToJsonSchema(mongoJsonSchemaResult);
    }

    // todo check accession logic in both POST and PUT requests
    public void saveSchemaWithAccession(@NonNull JsonSchema jsonSchema, Set<Field> importedFields) {
        String accession = jsonSchema.getAccession();
        if (accession != null && !accession.isEmpty()) {
            jsonSchema.setAccession(accession);
        }
        MongoJsonSchema mongoJsonSchema = modelConverter.jsonSchemaToMongoJsonSchema(jsonSchema);
        MongoJsonSchema mongoJsonSchemaResult = schemaRepository.save(mongoJsonSchema);

        // populate field groups
        Set<FieldGroup> groups = extractFieldGroups(importedFields);
        Map<String, FieldGroup> updatedGroups = new HashMap<>();
        groups.forEach(group -> {
            FieldGroup savedGroup = fieldGroupRepository.findByName(group.getName()).orElse(group);
            savedGroup.getFields().addAll(group.getFields());
            updatedGroups.put(savedGroup.getName(), savedGroup);
        });
        fieldGroupRepository.saveAll(updatedGroups.values());

        // populate fields
        importedFields.stream()
                .map(importedField -> {
                    Field fieldFromDb = fieldRepository.findById(importedField.getId()).orElse(importedField);
                    fieldFromDb.setGroup(updatedGroups.get(importedField.getGroup()).getId());
                    fieldFromDb.setType(importedField.getType());
                    fieldFromDb.setUnits(importedField.getUnits());
                    fieldFromDb.setLastModifiedDate(LocalDateTime.now());
                    return fieldFromDb;
                })
                .forEach(field -> {
                    field.getUsedBySchemas().add(jsonSchema.getId());
                    fieldRepository.save(field);
                });
    }

    public void deleteSchema(@NonNull String schemaId) {
        schemaRepository.deleteById(schemaId);
    }

    public boolean schemaIdExists(String schemaId) {
        Optional<MongoJsonSchema> schema = schemaRepository.findById(schemaId);
        return schema.isPresent();
    }

    public boolean schemaAccessionExists(String accession) {
        Optional<MongoJsonSchema> schema = schemaRepository.findFirstByAccessionOrderByVersionDesc(accession);
        return schema.isPresent();
    }

    public boolean schemaDomainAndNameExists(String domain, String name) {
        Optional<MongoJsonSchema> schema = schemaRepository.findFirstByDomainAndNameOrderByVersionDesc(domain, name);
        return schema.isPresent();
    }

    private void populateAccession(JsonSchema jsonSchema) {
        String accession = accessioningService.getSchemaAccession(jsonSchema.getId());
        jsonSchema.setAccession(accession);
    }

    public Page<MongoJsonSchema> findBySchema(MongoJsonSchema exampleSchema, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("schemaFieldAssociations")
                .withMatcher("searchable", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
        Example<MongoJsonSchema> example = Example.of(exampleSchema, matcher);
        return schemaRepository.findAll(example, pageable);
    }


    public List<SchemaRepository.AttributeResult> findAttributeValues(String attributeName){
        return schemaRepository.findAttributeValues(attributeName);
    }

    protected Set<FieldGroup> extractFieldGroups(Set<Field> fields) {
        Map<String, FieldGroup> groupMap = new HashMap<>();
        for (Field field : fields) {
            String groupName = field.getGroup().trim();
            FieldGroup group = groupMap.getOrDefault(groupName, new FieldGroup(groupName));
            group.getFields().add(field.getLabel());
            groupMap.putIfAbsent(groupName, group);
        }
        return new HashSet<>(groupMap.values());
    }

    public String updateSchemaFieldAssociationAndIncrementVersion(Field field, String oldFieldId, String schemaId) {
        log.info("Updating field: {} in schema: {}", field.getId(), schemaId);
        MongoJsonSchema schema = schemaRepository.findById(schemaId)
                .orElseThrow(() -> new DataIntegrityViolationException("Invalid schema reference: " + schemaId));
        SchemaFieldAssociation fieldAssociation = schema.getSchemaFieldAssociations().stream()
                .filter(f -> f.getFieldId().equals(oldFieldId))
                .findFirst()
                .orElseThrow(() -> new DataIntegrityViolationException(
                        "Expected field: " + oldFieldId + " could not be found in schema: " + schemaId));
        fieldAssociation.setFieldId(field.getId());
        processAndSaveCurrentVersionAsNonLatest(schema.getId());

        incrementMinorVersion(schemaId, schema);
        schemaRepository.save(schema);
        return schema.getId();
    }

    private static void incrementMinorVersion(String schemaId, MongoJsonSchema schema) {
        schema.setVersion(VersionIncrementer.incrementMinorVersion(schema.getVersion()));
        schema.setId(new SchemaId(schema.getAccession(), schema.getVersion()).asString());
        log.info("Updating schema: {} and incrementing version to: {}", schemaId, schema.getVersion());
    }

    public void processAndSaveCurrentVersionAsNonLatest(String schemaId) {
        MongoJsonSchema mongoSchema = schemaRepository.findById(schemaId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid checklistId: " + schemaId));
        mongoSchema.makeNonEditable();
        mongoSchema.makeNonLatest();
        schemaRepository.save(mongoSchema);
    }
}
