package bitone.akeneo.product_generator.domain.model;

import java.util.HashMap;
import bitone.akeneo.product_generator.domain.model.family.AttributeRequirement;

public class Family {

    private int id;
    private String code;
    private HashMap<String, String> labels;
    private Attribute[] attributes;
    private AttributeRequirement[] requirements;

    public Family(int id, String code, HashMap<String, String> labels, Attribute[] attributes, AttributeRequirement[] requirements) {
        this.id = id;
        this.code = code;
        this.labels = labels;
        this.attributes = attributes;
        this.requirements = requirements;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public HashMap<String, String> getLabels() {
        return labels;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public AttributeRequirement[] getAttributeRequirements() {
        return requirements;
    }
}
