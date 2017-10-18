package bitone.akeneo.product_generator.domain.generator.product_value;

import java.util.HashSet;
import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.model.attribute.Option;
import bitone.akeneo.product_generator.domain.RandomlyPicker;

class ValueOptionsGenerator implements ValueGenerator {
    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {
        Option[] options = attribute.getOptions();

        HashSet<Option> data = new HashSet<Option>();

        for (int i = 0; i < 3; i++) {
            Option option = options[RandomlyPicker.pickArrayIndex(options.length)];
            data.add(option);
        }

        return new ProductValue(attribute, data, locale, channel);
    }
}
