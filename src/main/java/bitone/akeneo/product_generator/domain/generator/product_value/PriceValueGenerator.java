package bitone.akeneo.product_generator.domain.generator.product_value;

import java.util.HashMap;
import java.util.ArrayList;
import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.model.Currency;
import bitone.akeneo.product_generator.domain.model.CurrencyRepository;
import bitone.akeneo.product_generator.domain.RandomlyPicker;

class PriceValueGenerator implements ValueGenerator {
    private CurrencyRepository currencyRepository;

    public PriceValueGenerator(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public ProductValue generate(Attribute attribute, Channel channel, Locale locale) {

        ArrayList<HashMap> data = new ArrayList<HashMap>();

        Currency[] currencies = currencyRepository.all();

        for (Currency currency: currencies) {
            HashMap<String, String> priceData = new HashMap<String, String>();
            int amount = RandomlyPicker.pickIntBetween(0, 1000);
            priceData.put("amount", String.valueOf(amount));
            priceData.put("currency", currency.getCode());

            data.add(priceData);
        }

        return new ProductValue(attribute, data, locale, channel);
    }
}
