package bitone.akeneo.product_generator.domain.model;

import bitone.akeneo.product_generator.domain.model.attribute.Properties;
import bitone.akeneo.product_generator.domain.model.attribute.Option;

public class Attribute {
    private int id;
    private String code;
    private String type;
    private boolean localizable;
    private boolean scopable;
    private Properties properties;
    private Option[] options;
    private AttributeGroup group;

    public Attribute(
        int id,
        String code,
        String type,
        boolean localizable,
        boolean scopable,
        Properties properties,
        Option[] options,
        AttributeGroup group
    ) {
        this.id = id;
        this.code = code;
        this.type = type;
        this.localizable = localizable;
        this.scopable = scopable;
        this.properties = properties;
        this.options = options;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public boolean isLocalizable() {
        return localizable;
    }

    public boolean isScopable() {
        return scopable;
    }

    public Properties getProperties() {
        return properties;
    }

    public Option[] getOptions() {
        return options;
    }

    public AttributeGroup getGroup() {
        return group;
    }
}
