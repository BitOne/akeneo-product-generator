package bitone.akeneo.product_generator.infrastructure.file;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import bitone.akeneo.product_generator.domain.model.ProductRepository;
import bitone.akeneo.product_generator.domain.model.Product;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.AttributeTypes;
import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Category;
import bitone.akeneo.product_generator.domain.model.attribute.Option;

public class FileProductRepository implements ProductRepository {
    private int uniqueDataId = 1;

    private PrintWriter productWriter;
    private PrintWriter productCategoryWriter;
    private PrintWriter uniqueDataWriter;

    public FileProductRepository(
        PrintWriter productWriter,
        PrintWriter productCategoryWriter,
        PrintWriter uniqueDataWriter
    ) {
        this.productWriter = productWriter;
        this.productCategoryWriter = productCategoryWriter;
        this.uniqueDataWriter = uniqueDataWriter;
    }

    public void add(Product product) {
        String valuesData = formatValues(product);

        productWriter.print(product.getId());
        productWriter.print('\t');
        productWriter.print(product.getFamily().getId());
        productWriter.print('\t');
        productWriter.print("\\N"); //product_model_id
        productWriter.print('\t');
        productWriter.print("\\N"); //family_variant_id
        productWriter.print('\t');
        productWriter.print("1");
        productWriter.print('\t');
        productWriter.print(product.getIdentifier());
        productWriter.print('\t');
        productWriter.print(valuesData.replace("\\", "\\\\"));
        productWriter.print('\t');
        productWriter.print("2017-10-12 10:07:10"); // created
        productWriter.print('\t');
        productWriter.print("2017-10-12 10:07:10"); // updated
        productWriter.print('\t');
        productWriter.println("product"); //type

        for (Category category : product.getCategories()) {
            productCategoryWriter.println(String.valueOf(product.getId()) + '\t' + String.valueOf(category.getId()));
        }

        for (ProductValue value: product.getValues()) {
            if (value.getAttribute().getProperties().isUnique()) {
                uniqueDataWriter.print(String.valueOf(uniqueDataId));
                uniqueDataWriter.print('\t');
                uniqueDataWriter.print(String.valueOf(product.getId()));
                uniqueDataWriter.print('\t');
                uniqueDataWriter.print(String.valueOf(value.getAttribute().getId()));
                uniqueDataWriter.print('\t');
                uniqueDataWriter.println(value.getData());
                uniqueDataId ++;
            }
        }
    }

    private String formatValues(Product product) {
        HashMap<String, HashMap> valuesArray = new HashMap<String, HashMap>();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'00:00:00+02:00").create();

        for (ProductValue value : product.getValues()) {
            Attribute attribute = value.getAttribute();
            HashMap<String, Object> valueArray = new HashMap<String, Object>();
            HashMap<String, HashMap> localeValueArray = new HashMap<String, HashMap>();

            Object data = value.getData();

            String channelCode = "<all_channels>";
            String localeCode = "<all_locales>";

            if (attribute.isScopable()) {
                channelCode = value.getChannel().getCode();
            }
            if (attribute.isLocalizable()) {
                localeCode = value.getLocale().getCode();
            }

            if (value.getAttribute().getType().equals(AttributeTypes.OPTION_SIMPLE_SELECT)) {
                String optionCode = ((Option) value.getData()).getCode();
                valueArray.put(localeCode, optionCode);
            } else if (value.getAttribute().getType().equals(AttributeTypes.OPTION_MULTI_SELECT)) {
                HashSet<Option> options = (HashSet<Option>) value.getData();

                String[] optionCodes = new String[options.size()];
                int i = 0;
                for (Option option: options) {
                    optionCodes[i] = option.getCode();
                    i++;
                }
                valueArray.put(localeCode, optionCodes);
            } else {
                valueArray.put(localeCode, value.getData());
            }
            localeValueArray.put(channelCode, valueArray);

            valuesArray.put(attribute.getCode(), localeValueArray);
        }

        return gson.toJson(valuesArray);
    }
}
