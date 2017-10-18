package bitone.akeneo.product_generator.domain.generator.product_value;

import java.util.HashMap;
import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.RandomlyPicker;

class MetricValueGenerator implements ValueGenerator {
    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {

        HashMap<String, Object> data = new HashMap<String, Object>();

        int amount = RandomlyPicker.pickIntBetween(0, 100);

        data.put("unit", attribute.getProperties().getDefaultMetricUnit());
        data.put("amount", new Integer(amount));
        data.put("family", attribute.getProperties().getMetricFamily());
        data.put("base_unit", attribute.getProperties().getDefaultMetricUnit());
        data.put("base_data", new Integer(amount));

        return new ProductValue(attribute, data, locale, channel);
    }
}
