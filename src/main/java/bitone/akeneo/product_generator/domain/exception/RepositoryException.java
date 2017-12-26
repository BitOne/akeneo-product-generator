package bitone.akeneo.product_generator.domain.exception;

import java.lang.Exception;

public class RepositoryException extends Exception {
    public RepositoryException(Exception e) {
        super(e);
    }
}
