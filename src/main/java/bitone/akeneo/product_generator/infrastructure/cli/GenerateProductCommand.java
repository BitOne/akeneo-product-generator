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
import bitone.akeneo.product_generator.domain.exception.RepositoryException;
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

    public void execute(
        String databaseUrl,
        String outputDirectory,
        int productCount,
        String productIndex,
        String productAndProductModelIndex
    )
        throws UnsupportedEncodingException, SQLException, NoFamilyDefinedException, NoChildrenCategoryDefinedException, SecurityException, RepositoryException {

        GenerateProductHandler handler;
        ProductGenerator generator;
        ProductRepository repository;

        repository = new FileProductRepository(outputDirectory, productIndex, productAndProductModelIndex);

        repository.open();

        generator = getGenerator(databaseUrl);
        handler = new GenerateProductHandler(generator, repository);

        for (int count = 0; count < productCount; count++) {
            GenerateProduct command = new GenerateProduct();
            handler.handle(command);
        }

        repository.close();
    }

    private ProductGenerator getGenerator(String databaseUrl) throws SQLException {
        LocaleRepository localeRepository = buildLocaleRepository(databaseUrl);
        CurrencyRepository currencyRepository = buildCurrencyRepository(databaseUrl);
        CategoryRepository categoryRepository = buildCategoryRepository(databaseUrl);
        ChannelRepository channelRepository = buildChannelRepository(databaseUrl, localeRepository, currencyRepository);
        AttributeRepository attributeRepository = buildAttributeRepository(databaseUrl);
        FamilyRepository familyRepository = buildFamilyRepository(databaseUrl, attributeRepository, channelRepository);

        return new ProductGenerator(
            channelRepository,
            localeRepository,
            currencyRepository,
            familyRepository,
            categoryRepository
        );
    }

    private CategoryRepository buildCategoryRepository(String databaseUrl) throws SQLException {
        DbCategoryRepository repository = new DbCategoryRepository();
        repository.initialize(databaseUrl);

        return repository;
    }

    private FamilyRepository buildFamilyRepository(
        String databaseUrl,
        AttributeRepository attributeRepository,
        ChannelRepository channelRepository
    ) throws SQLException {
        DbFamilyRepository repository = new DbFamilyRepository(attributeRepository, channelRepository);
        repository.initialize(databaseUrl);

        return repository;
    }

    private AttributeRepository buildAttributeRepository(String databaseUrl) throws SQLException {
        DbAttributeGroupRepository groupRepository = new DbAttributeGroupRepository();
        groupRepository.initialize(databaseUrl);

        DbAttributeRepository repository = new DbAttributeRepository();
        repository.initialize(databaseUrl);

        return repository;
    }

    private LocaleRepository buildLocaleRepository(String databaseUrl) throws SQLException {
        DbLocaleRepository repository = new DbLocaleRepository();
        repository.initialize(databaseUrl);

        return repository;
    }

    private CurrencyRepository buildCurrencyRepository(String databaseUrl) throws SQLException {
        DbCurrencyRepository repository = new DbCurrencyRepository();
        repository.initialize(databaseUrl);

        return repository;
    }

    private ChannelRepository buildChannelRepository(
        String databaseUrl,
        LocaleRepository localeRepository,
        CurrencyRepository currencyRepository
    ) throws SQLException {
        DbChannelRepository repository = new DbChannelRepository(localeRepository);
        repository.initialize(databaseUrl);

        return repository;
    }
}
