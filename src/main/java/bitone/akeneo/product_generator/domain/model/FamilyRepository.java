package bitone.akeneo.product_generator.domain.model;

public interface FamilyRepository {

    public Family get(String code);
    public int count();
    public Family[] all();
}
