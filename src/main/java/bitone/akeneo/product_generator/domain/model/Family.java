package bitone.akeneo.product_generator.domain.model;

import bitone.akeneo.product_generator.domain.model.family.AttributeRequirement;

public class Family {

    private int id;
    private String code;
    private Attribute[] attributes;
    private AttributeRequirement[] requirements;

    public Family(int id, String code, Attribute[] attributes, AttributeRequirement[] requirements) {
        this.id = id;
        this.code = code;
        this.attributes = attributes;
        this.requirements = requirements;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }

    public AttributeRequirement[] getAttributeRequirements() {
        return requirements;
    }
}
