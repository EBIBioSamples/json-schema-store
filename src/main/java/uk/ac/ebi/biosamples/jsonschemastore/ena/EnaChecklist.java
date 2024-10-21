package uk.ac.ebi.biosamples.jsonschemastore.ena;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "CHECKLIST_SET")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnaChecklist {
    @JsonProperty("CHECKLIST")
    private Checklist checklist;
}

@Data
class Checklist {
    @JacksonXmlProperty(isAttribute = true)
    private String accession;
    @JacksonXmlProperty(isAttribute = true)
    private String checklistType;
    @JacksonXmlProperty(localName = "DESCRIPTOR")
    private Descriptor descriptor;
}

@Data
class Descriptor {
    @JacksonXmlProperty(localName = "LABEL")
    private String label;
    @JacksonXmlProperty(localName = "NAME")
    private String name;
    @JacksonXmlProperty(localName = "DESCRIPTION")
    private String description;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "FIELD_GROUP")
    private List<FieldGroup> fieldGroups;
}

@Data
class FieldGroup {
    @JacksonXmlProperty(localName = "NAME")
    private String name;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "FIELD")
    private List<Field> fields;
}

@Data
class Field {
    @JacksonXmlProperty(localName = "LABEL")
    private String label;
    @JacksonXmlProperty(localName = "NAME")
    private String name;
    @JacksonXmlProperty(localName = "SYNONYM")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<String> synonyms;
    @JacksonXmlProperty(localName = "DESCRIPTION")
    private String description;
    @JacksonXmlProperty(localName = "MANDATORY")
    private String mandatory;
    @JacksonXmlProperty(localName = "MULTIPLICITY")
    private String multiplicity;
    @JacksonXmlProperty(localName = "FIELD_TYPE")
    private FieldType fieldType;
    @JacksonXmlProperty(localName = "UNITS")
    private List<String> units;
}

@Data
class FieldType {
    @JacksonXmlProperty(localName = "TEXT_FIELD")
    private TextField textField;
    @JacksonXmlProperty(localName = "TEXT_CHOICE_FIELD")
    private TextChoiceField textChoiceField;
    @JacksonXmlProperty(localName = "TAXON_FIELD")
    private TaxonField taxonField;
}

@Data
class TextField {
    @JacksonXmlProperty(localName = "REGEX_VALUE")
    private String regex;
}
@Data
class TaxonField {
    @JacksonXmlProperty(isAttribute = true)
    private String restrictionType;
}

@Data
class TextChoiceField {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TEXT_VALUE")
    private List<TextValue> textValue;
}

@Data
class TextValue {
    @JacksonXmlProperty(localName = "VALUE")
    private String value;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "SYNONYM")
    private List<String> synonyms;
}
