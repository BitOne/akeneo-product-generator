package bitone.akeneo.product_generator.domain.generator.product_value;

import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.model.ProductValue;

public interface ValueGenerator {

    public ProductValue generate(Attribute attribute, Channel channel, Locale locale);
}
