package bitone.akeneo.product_generator.domain.exception;

import java.lang.Exception;

public class NoFamilyDefinedException extends Exception {
    public NoFamilyDefinedException(String message) {
        super(message);
    }
}
