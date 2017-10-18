package bitone.akeneo.product_generator.domain.model;

public class ProductValue {
    private Attribute attribute;
    private Object data;
    private Locale locale;
    private Channel channel;

    public ProductValue(Attribute attribute, Object data, Locale locale, Channel channel) {
        this.attribute = attribute;
        this.data = data;
        this.locale = locale;
        this.channel = channel;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public Object getData() {
        return data;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public Channel getChannel()
    {
        return channel;
    }
}

