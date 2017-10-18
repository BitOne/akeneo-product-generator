package bitone.akeneo.product_generator.domain.model;

public class Channel {

    private int id;
    private String code;
    private Locale[] locales;
    private Currency[] currencies;

    public Channel(int id, String code, Locale[] locales, Currency[] currencies) {
        this.id  = id;
        this.code = code;
        this.locales = locales;
        this.currencies = currencies;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Locale[] getLocales() {
        return locales;
    }

    public Currency[] getCurrencies() {
        return currencies;
    }
}
