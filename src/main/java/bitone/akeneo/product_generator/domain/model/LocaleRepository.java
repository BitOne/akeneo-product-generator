package bitone.akeneo.product_generator.domain.model;

public interface LocaleRepository {
    public Locale get(String code);
    public int count();
    public Locale[] all();
}
