package bitone.akeneo.product_generator.domain.model;

public interface CategoryRepository {

    public Category get(String code);
    public int count();
    public Category[] all();
    public int countChildren();
    public Category[] allChildren();
}
