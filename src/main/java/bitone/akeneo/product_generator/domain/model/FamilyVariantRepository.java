package bitone.akeneo.product_generator.domain.model;

public interface FamilyVariantRepository {

    public FamilyVariant get(String code);
    public int count();
    public FamilyVariant[] all();
}
