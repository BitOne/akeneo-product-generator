package bitone.akeneo.product_generator.domain.model;

public class Currency {
    private int id;
    private String code;

    public Currency(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
}
