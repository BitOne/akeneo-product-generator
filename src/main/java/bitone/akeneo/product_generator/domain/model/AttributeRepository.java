package bitone.akeneo.product_generator.domain.model;

public interface AttributeRepository {

    public Attribute get(String code);
    public int count();
    public Attribute[] all();
}
