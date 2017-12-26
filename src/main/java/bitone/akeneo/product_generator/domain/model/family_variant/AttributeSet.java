package bitone.akeneo.product_generator.domain.model.family_variant;

import bitone.akeneo.product_generator.domain.model.Attribute;

public class AttributeSet {

    private int id;
    private int level;
    private Attribute[] axes;
    private Attribute[] attributes;

    public void AttributeSet(int id, int level, Attribute[] axes, Attribute[] attributes) {
        this.id = id;
        this.level = level;
        this.axes = axes;
        this.attributes = attributes;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public Attribute[] getAxes() {
        return axes;
    }

    public Attribute[] getAttributes() {
        return attributes;
    }
}
