package bitone.akeneo.product_generator.domain.model;

public class Product {

    private int id;
    private String identifier;
    private Family family;
    private Category[] categories;
    private ProductValue[] values;

    public Product(int id, String identifier, Family family, ProductValue[] values, Category[] categories) {
        this.id = id;
        this.identifier = identifier;
        this.family = family;
        this.categories = categories;
        this.values = values;
    }

    public int getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Family getFamily() {
        return family;
    }

    public Category[] getCategories() {
        return categories;
    }

    public ProductValue[] getValues() {
        return values;
    }
}
