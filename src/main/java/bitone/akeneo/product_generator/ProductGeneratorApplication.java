package bitone.akeneo.data_generator;

import bitone.akeneo.product_generator.infrastructure.cli.GenerateProductCommand;
import bitone.akeneo.product_generator.infrastructure.cli.GenerateProductCommand;
import bitone.akeneo.product_generator.domain.exception.NoFamilyDefinedException;
import bitone.akeneo.product_generator.domain.exception.NoChildrenCategoryDefinedException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;


public class ProductGeneratorApplication {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, SQLException, NoFamilyDefinedException, NoChildrenCategoryDefinedException {

        GenerateProductCommand generateProduct = new GenerateProductCommand();

        if (args.length == 0) {
            System.err.println("Usage: java -jar akeneo-product-generator.jar <database-url> <export-directory>");
            System.exit(1);
        }
        generateProduct.execute(args);
    }
}
