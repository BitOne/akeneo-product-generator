package bitone.akeneo.product_generator.domain.exception;

import java.lang.Exception;

public class NoChildrenCategoryDefinedException extends Exception {
    public NoChildrenCategoryDefinedException(String message) {
        super(message);
    }
}
