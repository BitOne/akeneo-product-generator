package bitone.akeneo.product_generator.domain.generator.product_value;

import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import com.github.javafaker.Faker;

class TextValueGenerator implements ValueGenerator {
    Faker faker = new Faker();

    long uniqueSuffix = 0;

    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {

        String data = faker.book().title();

        if (attribute.getProperties().isUnique()) {
            data += "-"+uniqueSuffix;
            uniqueSuffix++;
        }

        return new ProductValue(attribute, data, locale, channel);
    }
}
