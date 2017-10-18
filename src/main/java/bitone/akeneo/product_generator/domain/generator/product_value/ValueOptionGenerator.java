package bitone.akeneo.product_generator.domain.generator.product_value;

import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.model.attribute.Option;
import bitone.akeneo.product_generator.domain.RandomlyPicker;

class ValueOptionGenerator implements ValueGenerator {
    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {
        Option[] options = attribute.getOptions();

        Option data = options[RandomlyPicker.pickArrayIndex(options.length)];

        return new ProductValue(attribute, data, locale, channel);
    }
}
