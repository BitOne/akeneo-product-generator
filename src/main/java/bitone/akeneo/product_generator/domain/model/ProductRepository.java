package bitone.akeneo.product_generator.domain.model;

public interface ProductRepository {

    public void open();

    public void add(Product product);

    public void close();
}
