package bitone.akeneo.product_generator.infrastructure.file;

import bitone.akeneo.product_generator.domain.exception.RepositoryException;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.AttributeTypes;
import bitone.akeneo.product_generator.domain.model.Category;
import bitone.akeneo.product_generator.domain.model.Product;
import bitone.akeneo.product_generator.domain.model.ProductRepository;
import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.attribute.Option;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPOutputStream;

public class FileProductRepository implements ProductRepository {
    private int uniqueDataId = 1;
    private static final int esBatchSize = 90*1024*1024;

    private String outDir;
    private String productIndex;
    private String productAndProductModelIndex;

    private PrintWriter productWriter;
    private PrintWriter productCategoryWriter;
    private PrintWriter uniqueDataWriter;
    private PrintWriter esDataWriter;

    private Gson gson;
    private HashMap<String, String> esAttributeSuffixes;

    private int esFileCounter;
    private int esByteCounter;

    public FileProductRepository(
        String outDir,
        String productIndex,
        String productAndProductModelIndex
    ) {
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'00:00:00+02:00").create();
        this.esFileCounter = 0;
        this.esByteCounter = 0;
        this.esAttributeSuffixes = getElasticsearchAttributeSuffixes();

        this.outDir = outDir;
        this.productIndex = productIndex;
        this.productAndProductModelIndex = productAndProductModelIndex;
    }

    public void open() throws RepositoryException {
        try {
            File esDir = new File(outDir + "/es/");
            if (!esDir.exists()) {
                esDir.mkdir();
            }

            productWriter = new PrintWriter(outDir + "/products.tsv", StandardCharsets.UTF_8.name());
            productCategoryWriter = new PrintWriter(outDir + "/products-categories.tsv", StandardCharsets.UTF_8.name());
            uniqueDataWriter = new PrintWriter(outDir + "/products-unique-data.tsv", StandardCharsets.UTF_8.name());

            initEsOutput();

        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    public void close() throws RepositoryException {
        productWriter.close();
        productCategoryWriter.close();
        uniqueDataWriter.close();
        esDataWriter.close();
    }

    public void add(Product product) throws RepositoryException {
        try {
            writeProduct(product);
            writeProductCategories(product);
            writeUniqueData(product);
            writeEsData(product);
        } catch (IOException e) {
            throw new RepositoryException(e);
        }
    }

    private void writeProduct(Product product) {
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
    }

    private void writeProductCategories(Product product) {
        for (Category category : product.getCategories()) {
            productCategoryWriter.println(String.valueOf(product.getId()) + '\t' + String.valueOf(category.getId()));
        }
    }

    private void writeUniqueData(Product product) {

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
        HashMap<String, HashMap> valuesData = new HashMap<String, HashMap>();

        for (ProductValue value : product.getValues()) {
            Attribute attribute = value.getAttribute();

            String channelCode = "<all_channels>";
            String localeCode = "<all_locales>";

            if (attribute.isScopable()) {
                channelCode = value.getChannel().getCode();
            }
            if (attribute.isLocalizable()) {
                localeCode = value.getLocale().getCode();
            }

            Object data = value.getData();

            if (value.getAttribute().getType().equals(AttributeTypes.OPTION_SIMPLE_SELECT)) {
                if (value.getData() != null) {
                    data = ((Option) value.getData()).getCode();
                } else {
                    data = null;
                }
            } else if (value.getAttribute().getType().equals(AttributeTypes.OPTION_MULTI_SELECT)) {
                HashSet<Option> options = (HashSet<Option>) value.getData();

                String[] optionCodes = new String[options.size()];
                int i = 0;
                for (Option option: options) {
                    optionCodes[i] = option.getCode();
                    i++;
                }
                data = optionCodes;
            }

            if (null != data) {

                if (valuesData.containsKey(attribute.getCode())) {
                    HashMap<String, HashMap> valueData = valuesData.get(attribute.getCode());

                    if (valueData.containsKey(channelCode)) {
                        valueData.get(channelCode).put(localeCode, data);
                    } else {
                        HashMap<String, Object> localeAndData = new HashMap<String, Object>();
                        localeAndData.put(localeCode, data);

                        valueData.put(channelCode, localeAndData);
                    }
                } else {
                    HashMap<String, HashMap> channelAndLocale = new HashMap<String, HashMap>();
                    HashMap<String, Object> localeAndData = new HashMap<String, Object>();
                    localeAndData.put(localeCode, data);
                    channelAndLocale.put(channelCode, localeAndData);

                    valuesData.put(attribute.getCode(), channelAndLocale);
                }
            }
        }

        return gson.toJson(valuesData);
    }

    private String formatProductForElasticsearch(Product product) {
        HashMap<String, Object> productData = new HashMap<String, Object>();
        HashMap<String, Object> familyData = new HashMap<String, Object>();
        HashMap<String, HashMap> valuesData = new HashMap<String, HashMap>();
        HashMap<String, HashMap> label = new HashMap<String, HashMap>();
        ArrayList<String> attributesForThisLevel = new ArrayList<String>();

        Attribute attributeAsLabel = product.getFamily().getAttributeAsLabel();

        productData.put("id", product.getId());
        productData.put("identifier", product.getIdentifier());
        productData.put("created", "2017-10-12T10:07:10+01:00");
        productData.put("updated", "2017-10-12T10:07:10+01:00");
        productData.put("label", label);

        familyData.put("code", product.getFamily().getCode());
        familyData.put("labels", product.getFamily().getLabels());

        productData.put("family", familyData);

        productData.put("enabled", product.isEnabled());

        productData.put("document_type", "Pim\\Component\\Catalog\\Model\\ProductInterface");

        ArrayList<String> categories = new ArrayList<String>();

        for (Category category : product.getCategories()) {
            categories.add(category.getCode());
        }

        productData.put("categories", categories);

        for (Attribute attribute: product.getFamily().getAttributes()) {
            attributesForThisLevel.add(attribute.getCode());
        }

        productData.put("attributes_for_this_level", attributesForThisLevel);

        for (ProductValue value : product.getValues()) {
            Attribute attribute = value.getAttribute();

            if (attribute.getType().equals(AttributeTypes.IDENTIFIER)) {
                continue;
            }

            String esAttributeCode = attribute.getCode() + esAttributeSuffixes.get(attribute.getType());

            String channelCode = "<all_channels>";
            String localeCode = "<all_locales>";

            if (attribute.isScopable()) {
                channelCode = value.getChannel().getCode();
            }
            if (attribute.isLocalizable()) {
                localeCode = value.getLocale().getCode();
            }

            Object data = value.getData();

            if (value.getAttribute().getType().equals(AttributeTypes.OPTION_SIMPLE_SELECT)) {
                if (value.getData() != null) {
                    data = ((Option) value.getData()).getCode();
                } else {
                    data = null;
                }
            } else if (value.getAttribute().getType().equals(AttributeTypes.OPTION_MULTI_SELECT)) {
                HashSet<Option> options = (HashSet<Option>) value.getData();

                String[] optionCodes = new String[options.size()];
                int i = 0;
                for (Option option: options) {
                    optionCodes[i] = option.getCode();
                    i++;
                }
                data = optionCodes;
            } else if (value.getAttribute().getType().equals(AttributeTypes.PRICE_COLLECTION)) {
                HashMap<String, String> priceData = new HashMap<String, String>();

                for (HashMap<String, String> price : (ArrayList<HashMap>) value.getData()) {
                    priceData.put(price.get("currency"), price.get("amount"));
                }

                data = priceData;
            }

            if (null != data) {

                if (valuesData.containsKey(esAttributeCode)) {
                    HashMap<String, HashMap> valueData = valuesData.get(esAttributeCode);

                    if (valueData.containsKey(channelCode)) {
                        valueData.get(channelCode).put(localeCode, data);
                    } else {
                        HashMap<String, Object> localeAndData = new HashMap<String, Object>();
                        localeAndData.put(localeCode, data);

                        valueData.put(channelCode, localeAndData);
                    }
                } else {
                    HashMap<String, HashMap> channelAndLocale = new HashMap<String, HashMap>();
                    HashMap<String, Object> localeAndData = new HashMap<String, Object>();
                    localeAndData.put(localeCode, data);
                    channelAndLocale.put(channelCode, localeAndData);

                    valuesData.put(esAttributeCode, channelAndLocale);
                }

                if (attribute.getCode().equals(attributeAsLabel.getCode())) {
                    HashMap<String, HashMap> valueData = valuesData.get(attribute.getCode());

                    if (label.containsKey(channelCode)) {
                        label.get(channelCode).put(localeCode, data);
                    } else {
                        HashMap<String, Object> localeAndData = new HashMap<String, Object>();
                        localeAndData.put(localeCode, data);

                        label.put(channelCode, localeAndData);
                    }
                }
            }
        }

        productData.put("values", valuesData);

        return gson.toJson(productData);
    }

    private HashMap<String, String> getElasticsearchAttributeSuffixes() {
        HashMap<String, String> attributeSuffixes = new HashMap<String, String>();

        attributeSuffixes.put(AttributeTypes.BOOLEAN, "-boolean");
        attributeSuffixes.put(AttributeTypes.DATE, "-date");
        attributeSuffixes.put(AttributeTypes.METRIC, "-metric");
        attributeSuffixes.put(AttributeTypes.NUMBER, "-decimal");
        attributeSuffixes.put(AttributeTypes.OPTION_MULTI_SELECT, "-options");
        attributeSuffixes.put(AttributeTypes.OPTION_SIMPLE_SELECT, "-option");
        attributeSuffixes.put(AttributeTypes.PRICE_COLLECTION, "-prices");
        attributeSuffixes.put(AttributeTypes.TEXTAREA, "-textarea");
        attributeSuffixes.put(AttributeTypes.TEXT, "-text");

        return attributeSuffixes;
    }

    private void writeEsData(Product product) throws IOException {

        String esProductData = formatProductForElasticsearch(product);

        writeToEsOutput(
            String.format(
                "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"pim_catalog_product\", \"_id\" : \"%s\" } }%n%s%n",
                productIndex,
                product.getId(),
                esProductData
            )
        );

        esProductData = esProductData.replaceAll("\"id\":([0-9]+)", "\"id\":\"product_$1\"");
        writeToEsOutput(
            String.format(
                "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"pim_catalog_product\", \"_id\" : \"product_%s\" } }%n%s%n",
                productAndProductModelIndex,
                product.getId(),
                esProductData
            )
        );
    }

    private void initEsOutput() throws FileNotFoundException, IOException {
        OutputStreamWriter esOutputStream= new OutputStreamWriter(
            new GZIPOutputStream(
                new FileOutputStream(outDir + "/es/es-data-" + esFileCounter + ".gzip")
            ),
            StandardCharsets.UTF_8
        );

        esDataWriter = new PrintWriter(esOutputStream);
    }

    private void writeToEsOutput(String data) throws FileNotFoundException, IOException {

        int dataLength = data.toString().getBytes(StandardCharsets.UTF_8.name()).length;

        if (esByteCounter + dataLength >= esBatchSize) {
            esDataWriter.close();
            esFileCounter++;

            esByteCounter = 0;

            initEsOutput();
        }

        esDataWriter.print(data);
        esByteCounter += dataLength;
    }
}
