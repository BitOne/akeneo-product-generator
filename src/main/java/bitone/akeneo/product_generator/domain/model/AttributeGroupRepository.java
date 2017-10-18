package bitone.akeneo.product_generator.domain.model;

public interface AttributeGroupRepository {

    public AttributeGroup get(String code);
    public int count();
    public AttributeGroup[] all();
}
