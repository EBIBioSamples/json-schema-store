package uk.ac.ebi.biosamples.jsonschemastore.model;

import lombok.Data;

@Data
public class ChecklistField {
    private String name;
    private String description;
    private boolean required;
    private boolean recommended;

    private String type;
    private String format;
    private String pattern;
    private int minLength;
    private int maxLength;
    private double min;
    private double max;
    private double multiples;
    private String enums;
    private String constant;
}
