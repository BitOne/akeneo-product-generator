package bitone.akeneo.product_generator.domain.generator;

import bitone.akeneo.product_generator.domain.RandomlyPicker;
import bitone.akeneo.product_generator.domain.exception.NoChildrenCategoryDefinedException;
import bitone.akeneo.product_generator.domain.exception.NoFamilyDefinedException;
import bitone.akeneo.product_generator.domain.generator.product_value.ValueGenerator;
import bitone.akeneo.product_generator.domain.generator.product_value.ValueGeneratorRegistry;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.AttributeTypes;
import bitone.akeneo.product_generator.domain.model.Category;
import bitone.akeneo.product_generator.domain.model.CategoryRepository;
import bitone.akeneo.product_generator.domain.model.Channel;
import bitone.akeneo.product_generator.domain.model.ChannelRepository;
import bitone.akeneo.product_generator.domain.model.CurrencyRepository;
import bitone.akeneo.product_generator.domain.model.Family;
import bitone.akeneo.product_generator.domain.model.FamilyRepository;
import bitone.akeneo.product_generator.domain.model.Locale;
import bitone.akeneo.product_generator.domain.model.LocaleRepository;
import bitone.akeneo.product_generator.domain.model.Product;
import bitone.akeneo.product_generator.domain.model.ProductValue;
import java.util.ArrayList;
import java.util.HashSet;

public class ProductGenerator
{
    private ChannelRepository channelRepository;
    private LocaleRepository localeRepository;
    private CurrencyRepository currencyRepository;
    private FamilyRepository familyRepository;
    private CategoryRepository categoryRepository;
    private ValueGeneratorRegistry valueGeneratorRegistry;
    private int productIdIndex;
    private int productIdentifierIndex;

    public ProductGenerator (
        ChannelRepository channelRepository,
        LocaleRepository localeRepository,
        CurrencyRepository currencyRepository,
        FamilyRepository familyRepository,
        CategoryRepository categoryRepository
    ) {
        this.channelRepository = channelRepository;
        this.localeRepository = localeRepository;
        this.currencyRepository = currencyRepository;
        this.familyRepository = familyRepository;
        this.categoryRepository = categoryRepository;
        this.valueGeneratorRegistry = new ValueGeneratorRegistry(currencyRepository);

        this.productIdentifierIndex = 0;
        this.productIdIndex = 0;
    }

    public Product generate() throws
        NoFamilyDefinedException,
        NoChildrenCategoryDefinedException {

        String identifier = generateUniqueIdentifier();
        int id = generateUniqueId();
        Family family = getRandomFamily();
        ProductValue[] values = getRandomValues(family, identifier);
        Category[] categories = getRandomCategories();

        return new Product(id, identifier, family, values, categories);
    }

    private Family getRandomFamily() throws NoFamilyDefinedException {
        Family[] families;

        if (familyRepository.count() == 0) {
            throw new NoFamilyDefinedException("At least one family should exist.");
        }
        families = familyRepository.all();

        if (families.length == 1) {
            return families[0];
        } else {
            return families[RandomlyPicker.pickArrayIndex(familyRepository.count())];
        }
    }

    private Category[] getRandomCategories() throws NoChildrenCategoryDefinedException {

        HashSet<Category> randomCategories = new HashSet<Category>();
        Category[] allCategories;

        if (categoryRepository.countChildren() == 0) {
            throw new NoChildrenCategoryDefinedException("At least one children category should exist");
        }

        allCategories = categoryRepository.allChildren();
        for (int i = 0; i < 4; i++) {
            Category category = allCategories[RandomlyPicker.pickArrayIndex(allCategories.length)];

            randomCategories.add(category);
        }

        return (Category[]) randomCategories.toArray(new Category[randomCategories.size()]);
    }

    private ProductValue[] getRandomValues(Family family, String identifier)
    {
        Attribute[] attributes = family.getAttributes();
        ArrayList<ProductValue> values = new ArrayList<ProductValue>();

        for (Attribute attribute : attributes) {
            if (attribute.getType().equals(AttributeTypes.IDENTIFIER)) {
                ProductValue identifierValue = new ProductValue(attribute, identifier, null, null);
                values.add(identifierValue);
            }

            generateValues(values, attribute);
        }

        return (ProductValue[]) values.toArray(new ProductValue[values.size()]);
    }

    private void generateValues(ArrayList<ProductValue> values, Attribute attribute)
    {
        if (!valueGeneratorRegistry.support(attribute)) {
            return;
        }
        ValueGenerator generator = valueGeneratorRegistry.get(attribute);

        if (attribute.isScopable() && attribute.isLocalizable()) {
            for (Channel channel: channelRepository.all()) {
                for (Locale locale: channel.getLocales()) {
                    values.add(generator.generate(attribute, channel, locale));
                }
            }
        } else if (attribute.isScopable()) {
            for (Channel channel: channelRepository.all()) {
                values.add(generator.generate(attribute, channel, null));
            }
        } else if (attribute.isLocalizable()) {
            for (Locale locale: this.localeRepository.all()) {
                values.add(generator.generate(attribute, null, locale));
            }
        } else {
            values.add(generator.generate(attribute, null, null));
        }
    }

    private String generateUniqueIdentifier() {
        productIdentifierIndex++;

        return "sku-" + productIdentifierIndex;
    }

    private int generateUniqueId() {
        productIdIndex++;

        return productIdIndex;
    }

    public void reset() {
        productIdentifierIndex = 0;
    }
}
