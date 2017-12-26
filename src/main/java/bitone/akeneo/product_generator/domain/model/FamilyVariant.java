package bitone.akeneo.product_generator.domain.model;

import bitone.akeneo.product_generator.domain.model.family_variant.AttributeSet;
import java.util.ArrayList;

public class FamilyVariant {

    private int id;
    private String code;
    private Family family;
    private AttributeSet[] attributeSets;

    public FamilyVariant(
        int id,
        String code,
        Family family,
        AttributeSet[] attributeSets
    ) {

        this.id = id;
        this.family = family;
        this.code = code;
        this.attributeSets = attributeSets;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Family getFamily() {
        return family;
    }

    public AttributeSet[] getAttributeSets() {
        return attributeSets;
    }
}
