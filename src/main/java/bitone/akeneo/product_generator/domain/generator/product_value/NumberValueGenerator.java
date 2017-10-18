package bitone.akeneo.product_generator.domain.generator.product_value;

import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.RandomlyPicker;

class NumberValueGenerator implements ValueGenerator {
    private static int defaultMin = 0;
    private static int defaultMax = 1000;

    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {
        int min = defaultMin;
        int max = defaultMax;

        if (attribute.getProperties().getNumberMin() != null) {
            min = attribute.getProperties().getNumberMin().intValue();
        }

        if (attribute.getProperties().getNumberMax() != null) {
            max = attribute.getProperties().getNumberMax().intValue();
        }

        Integer data = new Integer(RandomlyPicker.pickIntBetween(min, max));

        return new ProductValue(attribute, data, locale, channel);
    }
}
