package bitone.akeneo.product_generator.domain.generator.product_value;

import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.RandomlyPicker;
import java.util.Date;
import java.util.Calendar;

class DateValueGenerator implements ValueGenerator {
    private static Date defaultMin;
    private static Date defaultMax;

    public DateValueGenerator() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(1970, 1, 1);
        defaultMin = calendar.getTime();
        calendar.set(2020, 1, 1);
        defaultMax = calendar.getTime();
    }

    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {
        Date min = defaultMin;
        Date max = defaultMax;

        if (attribute.getProperties().getDateMin() != null) {
            min = attribute.getProperties().getDateMin();
        }

        if (attribute.getProperties().getDateMax() != null) {
            max = attribute.getProperties().getDateMax();
        }

        Date data = RandomlyPicker.pickDateBetween(min, max);

        return new ProductValue(attribute, data, locale, channel);
    }
}
