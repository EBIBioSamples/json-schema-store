package uk.ac.ebi.biosamples.jsonschemastore.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.ac.ebi.biosamples.jsonschemastore.ena.SchemaTemplateGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static uk.ac.ebi.biosamples.jsonschemastore.service.VariableNameFormatter.toVariableName;

@Data
@Document(collection = "fields")
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        use = JsonTypeInfo.Id.NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChoiceField.class, name = "choice"),
        @JsonSubTypes.Type(value = PatternField.class, name = "pattern"),
        @JsonSubTypes.Type(value = Field.class, name = "text")
})
public class Field
{
    @Id
    protected String id;
    protected String name;
    protected String version;
    protected String label;
    protected String description;
    protected Set<String> usedBySchemas = new HashSet<>();
    protected String type;
    protected String group;
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public static Field fromProperty(Property property) {
        JsonNode typeAsJson = SchemaTemplateGenerator.getJson(property.type());
        Field.FieldBuilder builder;
        if(typeAsJson.has("enum")) {
            List<String> choices = StreamSupport.stream(typeAsJson.get("enum").spliterator(), false)
                    .map(JsonNode::asText)
                    .collect(Collectors.toList());
            builder = ChoiceField.builder().type("choice");
             ((ChoiceField.ChoiceFieldBuilder<?, ?>) builder)
                     .choices(choices);
        } else if (typeAsJson.has("pattern")) {
            builder = PatternField.builder().type("pattern");
            ((PatternField.PatternFieldBuilder<?, ?>) builder)
                    .pattern(typeAsJson.get("pattern").asText());
        } else if (typeAsJson.has("type")) {
            builder = Field.builder().type("string");
        } else {
            throw new IllegalArgumentException("property " + property.name() + " has unsupported type " + property.type());
        }
        return builder.name(toVariableName(property.name()))
                .description(property.description())
                .version("1.0")
                .id(new FieldId(toVariableName(property.name()), "1.0").asString())
                .label(property.name())
                .usedBySchemas(new HashSet<>(1))
                .build();

    }
}

