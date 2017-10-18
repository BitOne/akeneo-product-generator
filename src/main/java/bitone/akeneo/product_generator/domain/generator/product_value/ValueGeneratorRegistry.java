package bitone.akeneo.product_generator.domain.generator.product_value;

import bitone.akeneo.product_generator.domain.model.CurrencyRepository;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.AttributeTypes;

import java.util.HashMap;

public class ValueGeneratorRegistry {
    private HashMap<String, ValueGenerator> generators;

    public ValueGeneratorRegistry(CurrencyRepository currencyRepository) {
        generators = new HashMap<String, ValueGenerator>();

        generators.put(AttributeTypes.TEXTAREA, new TextAreaValueGenerator());
        generators.put(AttributeTypes.TEXT, new TextValueGenerator());
        generators.put(AttributeTypes.BOOLEAN,  new BooleanValueGenerator());
        generators.put(AttributeTypes.OPTION_SIMPLE_SELECT, new ValueOptionGenerator());
        generators.put(AttributeTypes.OPTION_MULTI_SELECT, new ValueOptionsGenerator());
        generators.put(AttributeTypes.METRIC, new MetricValueGenerator());
        generators.put(AttributeTypes.PRICE_COLLECTION, new PriceValueGenerator(currencyRepository));
        generators.put(AttributeTypes.NUMBER, new NumberValueGenerator());
        generators.put(AttributeTypes.DATE, new DateValueGenerator());
        /*
        generators.put(AttributeTypes.IMAGE, new ProductValueImageGenerator());
        */
    }

    public boolean support(Attribute attribute) {
        return generators.containsKey(attribute.getType());
    }

    public ValueGenerator get(Attribute attribute) {
        return generators.get(attribute.getType());
    }
}

