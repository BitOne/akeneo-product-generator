package bitone.akeneo.product_generator.domain.generator.product_value;

import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import com.github.javafaker.Faker;

class TextAreaValueGenerator implements ValueGenerator {
    Faker faker;

    public TextAreaValueGenerator() {
        faker = new Faker();
    }

    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {

        String data = faker.shakespeare().hamletQuote();

        return new ProductValue(attribute, data, locale, channel);
    }
}
