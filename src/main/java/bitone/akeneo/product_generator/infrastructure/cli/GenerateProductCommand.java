package bitone.akeneo.product_generator.infrastructure.cli;

import bitone.akeneo.product_generator.domain.generator.ProductGenerator;
import bitone.akeneo.product_generator.domain.model.AttributeGroupRepository;
import bitone.akeneo.product_generator.domain.model.AttributeRepository;
import bitone.akeneo.product_generator.domain.model.CategoryRepository;
import bitone.akeneo.product_generator.domain.model.ChannelRepository;
import bitone.akeneo.product_generator.domain.model.CurrencyRepository;
import bitone.akeneo.product_generator.domain.model.FamilyRepository;
import bitone.akeneo.product_generator.domain.model.LocaleRepository;
import bitone.akeneo.product_generator.domain.model.ProductRepository;
import bitone.akeneo.product_generator.domain.exception.NoFamilyDefinedException;
import bitone.akeneo.product_generator.domain.exception.NoChildrenCategoryDefinedException;
import bitone.akeneo.product_generator.infrastructure.database.DbAttributeGroupRepository;
import bitone.akeneo.product_generator.infrastructure.database.DbAttributeRepository;
import bitone.akeneo.product_generator.infrastructure.database.DbCategoryRepository;
import bitone.akeneo.product_generator.infrastructure.database.DbChannelRepository;
import bitone.akeneo.product_generator.infrastructure.database.DbCurrencyRepository;
import bitone.akeneo.product_generator.infrastructure.database.DbFamilyRepository;
import bitone.akeneo.product_generator.infrastructure.database.DbLocaleRepository;
import bitone.akeneo.product_generator.infrastructure.file.FileProductRepository;
import bitone.akeneo.product_generator.application.GenerateProductHandler;
import bitone.akeneo.product_generator.application.GenerateProduct;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class GenerateProductCommand {
    private String dbUrl;

    public void execute(String[] args)
        throws FileNotFoundException, UnsupportedEncodingException, SQLException, NoFamilyDefinedException, NoChildrenCategoryDefinedException {

        GenerateProductHandler handler;
        ProductGenerator generator;
        ProductRepository repository;

        dbUrl = args[0];
        String exportDir = args[1];
        int productsCount = Integer.valueOf(args[2]);

        PrintWriter productWriter = new PrintWriter(exportDir + "/products.tsv");
        PrintWriter productCategoryWriter = new PrintWriter(exportDir + "/products-categories.tsv");
        PrintWriter uniqueDataWriter = new PrintWriter(exportDir + "/products-unique-data.tsv");

        repository = getProductRepository(productWriter, productCategoryWriter, uniqueDataWriter);
        generator = getGenerator();
        handler = new GenerateProductHandler(generator, repository);

       for (int count = 0; count < productsCount; count++) {
           GenerateProduct command = new GenerateProduct();
           handler.handle(command);
        }

        productWriter.close();
        productCategoryWriter.close();
        uniqueDataWriter.close();
    }

    private ProductRepository getProductRepository(PrintWriter productWriter, PrintWriter productCategoryWriter, PrintWriter uniqueDataWriter) {
        return new FileProductRepository(productWriter, productCategoryWriter, uniqueDataWriter);
    }

    private ProductGenerator getGenerator() throws SQLException {
        LocaleRepository localeRepository = buildLocaleRepository();
        CurrencyRepository currencyRepository = buildCurrencyRepository();
        CategoryRepository categoryRepository = buildCategoryRepository();
        ChannelRepository channelRepository = buildChannelRepository(localeRepository, currencyRepository);
        AttributeRepository attributeRepository = buildAttributeRepository();
        FamilyRepository familyRepository = buildFamilyRepository(attributeRepository, channelRepository);

        return new ProductGenerator(
            channelRepository,
            localeRepository,
            currencyRepository,
            familyRepository,
            categoryRepository
        );
    }

    private CategoryRepository buildCategoryRepository() throws SQLException {
        DbCategoryRepository repository = new DbCategoryRepository();
        repository.initialize(dbUrl);

        return repository;
    }

    private FamilyRepository buildFamilyRepository(
        AttributeRepository attributeRepository,
        ChannelRepository channelRepository
    ) throws SQLException {
        DbFamilyRepository repository = new DbFamilyRepository(attributeRepository, channelRepository);
        repository.initialize(dbUrl);

        return repository;
    }

    private AttributeRepository buildAttributeRepository() throws SQLException {
        DbAttributeGroupRepository groupRepository = new DbAttributeGroupRepository();
        groupRepository.initialize(dbUrl);

        DbAttributeRepository repository = new DbAttributeRepository();
        repository.initialize(dbUrl);

        return repository;
    }

    private LocaleRepository buildLocaleRepository() throws SQLException {
        DbLocaleRepository repository = new DbLocaleRepository();
        repository.initialize(dbUrl);

        return repository;
    }

    private CurrencyRepository buildCurrencyRepository() throws SQLException {
        DbCurrencyRepository repository = new DbCurrencyRepository();
        repository.initialize(dbUrl);

        return repository;
    }

    private ChannelRepository buildChannelRepository(
        LocaleRepository localeRepository,
        CurrencyRepository currencyRepository
    ) throws SQLException {
        DbChannelRepository repository = new DbChannelRepository(localeRepository);
        repository.initialize(dbUrl);

        return repository;
    }
}
