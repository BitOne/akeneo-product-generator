package bitone.akeneo.product_generator.application;

import bitone.akeneo.product_generator.domain.generator.ProductGenerator;
import bitone.akeneo.product_generator.domain.model.ProductRepository;
import bitone.akeneo.product_generator.domain.model.Product;
import bitone.akeneo.product_generator.domain.exception.NoFamilyDefinedException;
import bitone.akeneo.product_generator.domain.exception.NoChildrenCategoryDefinedException;

public class GenerateProductHandler {
    private ProductGenerator generator;
    private ProductRepository repository;

    public GenerateProductHandler(ProductGenerator generator, ProductRepository repository) {
        this.generator = generator;
        this.repository = repository;
    }

    public void handle(GenerateProduct command) throws
        NoFamilyDefinedException,
        NoChildrenCategoryDefinedException
    {
        Product product = generator.generate();

        repository.add(product);
    }

}
