package bitone.akeneo.product_generator.domain.model;

public interface CurrencyRepository {
    public Currency get(String code);
    public int count();
    public Currency[] all();
}
