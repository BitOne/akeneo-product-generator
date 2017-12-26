package bitone.akeneo.product_generator.domain.model;

import bitone.akeneo.product_generator.domain.exception.RepositoryException;

public interface ProductRepository {

    public void open() throws RepositoryException;

    public void add(Product product) throws RepositoryException;

    public void close() throws RepositoryException;
}
