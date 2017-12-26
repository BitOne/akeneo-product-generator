package bitone.akeneo.product_generator.infrastructure.file;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import bitone.akeneo.product_generator.domain.model.ProductRepository;
import bitone.akeneo.product_generator.domain.model.ProductRepository;
import bitone.akeneo.product_generator.domain.model.Product;
import bitone.akeneo.product_generator.domain.model.Attribute;
import bitone.akeneo.product_generator.domain.model.AttributeTypes;
import bitone.akeneo.product_generator.domain.model.ProductValue;
import bitone.akeneo.product_generator.domain.model.Category;
import bitone.akeneo.product_generator.domain.model.attribute.Option;

public class FileProductModelRepository implements ProductModelRepository {
    private static final int esBatchSize = 100;

    private String outDir;
    private String productModelIndex;

    private PrintWriter productModelWriter;
    private PrintWriter productModelCategoryWriter;
    private FileOutputStream esOutputStream;
    private GZIPOutputStream esCompressedStream;
    private PrintWriter elasticsearchDataWriter;

    private Gson gson;
    private HashMap<String, String> esAttributeSuffixes;
    private int productModelsCounter;

    public FileProductModelRepository(
        String outDir,
        String productModelIndex
    ) {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'00:00:00+02:00").create();

        this.outDir = outDir;
        this.productModelIndex = productModelIndex;

        esAttributeSuffixes = getElasticsearchAttributeSuffixes();
    }

    public void open() {
        try {
            File esDir = new File(outDir + "/es/");
            if (!esDir.exists()) {
                esDir.mkdir();
            }

            productModelWriter = new PrintWriter(outDir + "/product-models.tsv");
            productModelCategoryWriter = new PrintWriter(outDir + "/product-models-categories.tsv");
        } catch (Exception e) {
            System.err.println("Unable to open the repository:" + e.getMessage());
        }

        productModelsCounter = 0;
    }

    public void close() {
        try {
            productModelWriter.close();
            productModelCategoryWriter.close();
            elasticsearchDataWriter.close();
            esCompressedStream.close();
        } catch (Exception e) {
            System.err.println("Unable to close the repository:" + e.getMessage());
        }
    }

    public void add(ProductModel productModel) {
        if (productModelsCounter % esBatchSize == 0) {
            try {
                esOutputStream = new FileOutputStream(outDir + "/es/es-data-" + productModelsCounter + ".gzip");
                esCompressedStream = new GZIPOutputStream(esOutputStream);
                elasticsearchDataWriter = new PrintWriter(esCompressedStream);
            } catch (Exception e) {
                System.err.println("Unable to open ES data file: "+e.getMessage());
            }
        }

        String valuesData = formatValues(productModel);

        productModelWriter.print(productModel.getId());
        productModelWriter.print('\t');
        if (productModel.getParent() != null) {
            productModelWriter.print(productModel.getParent().getId());
        } else {
            productModelWriter.print("\\N");
        }
        productModelWriter.print('\t');
        productModelWriter.print(productModel.getFamilyVariant().getId());
        productModelWriter.print('\t');
        productModelWriter.print(productModel.getCode());
        productModelWriter.print('\t');
        productModelWriter.print(valuesData.replace("\\", "\\\\"));
        productModelWriter.print('\t');
        productModelWriter.print("2017-10-12 10:07:10"); // created
        productModelWriter.print('\t');
        productModelWriter.print("2017-10-12 10:07:10"); // updated
        productModelWriter.print('\t');
        productModelWriter.print(productModel.getRoot().getId());
        productModelWriter.print('\t');
        productModelWriter.print(productModel.getLevel());
        productModelWriter.print('\t');
        productModelWriter.print(productModel.getLeft());
        productModelWriter.print('\t');
        productModelWriter.print(productModel.getRight());

        for (Category category : productModel.getCategories()) {
            productModelCategoryWriter.println(String.valueOf(productModel.getId()) + '\t' + String.valueOf(category.getId()));
        }

        String esProductModel = formatProductModelForElasticsearch(productModel);

        elasticsearchDataWriter.format(
            "{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"pim_catalog_product\", \"_id\" : \"%s\" } }%n",
            productModelIndex,
            productModel.getId()
        );
        elasticsearchDataWriter.println(esProductModel);

        productModelsCounter++;

        if (productModelsCounter % esBatchSize == 0) {
            try {
                elasticsearchDataWriter.close();
                esCompressedStream.close();
                esOutputStream.close();
            } catch (Exception e) {
                System.err.println("Unable to close ES file: "+e.getMessage());
            }
        }
    }

    private String formatValues(ProductModel productModel) {
        HashMap<String, HashMap> valuesData = new HashMap<String, HashMap>();

        for (ProductValue value : productModel.getValues()) {
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

    private String formatProductModelForElasticsearch(ProductModel productModel) {
        HashMap<String, Object> productModelData = new HashMap<String, Object>();
        HashMap<String, Object> familyData = new HashMap<String, Object>();
        HashMap<String, HashMap> valuesData = new HashMap<String, HashMap>();
        ArrayList<String> attributesForThisLevel = new ArrayList<String>();

        productModelData.put("id", productModel.getId());
        productModelData.put("identifier", productModel.getIdentifier());
        productModelData.put("created", "2017-10-12T10:07:10+01:00");
        productModelData.put("updated", "2017-10-12T10:07:10+01:00");

        familyData.put("code", productModel.getFamilyVariant().getFamily().getCode());
        familyData.put("labels", productModel.getFamilyVariant().getFamily().getLabels());

        productModelData.put("family_variant", productModel.getFamilyVariant().getCode());

        productModelData.put("document_type", "Pim\\Component\\Catalog\\Model\\ProductModelInterface");

        ArrayList<String> categories = new ArrayList<String>();

        for (Category category : productModel.getCategories()) {
            categories.add(category.getCode());
        }

        productModelData.put("categories", categories);

        for (Attribute attribute: productModel.getFamilyVariant().getAttributes()) {
            attributesForThisLevel.add(attribute.getCode());
        }

        productModelData.put("attributes_for_this_level", attributesForThisLevel);

        for (ProductValue value : productModel.getValues()) {
            Attribute attribute = value.getAttribute();

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
            }
        }

        productModelData.put("values", valuesData);

        return gson.toJson(productModelData);
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
}
