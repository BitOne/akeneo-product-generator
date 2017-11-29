package bitone.akeneo.data_generator;

import bitone.akeneo.product_generator.infrastructure.cli.GenerateProductCommand;
import bitone.akeneo.product_generator.domain.exception.NoFamilyDefinedException;
import bitone.akeneo.product_generator.domain.exception.NoChildrenCategoryDefinedException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option.Builder;

public class ProductGeneratorApplication {

    final private static String defaultProductIndex = "akeneo_pim_product";
    final private static String defaultProductAndProductModelIndex = "akeneo_pim_product_and_product_model";

    public static void main(String[] args) throws
        FileNotFoundException,
        UnsupportedEncodingException,
        SQLException,
        NoFamilyDefinedException,
        NoChildrenCategoryDefinedException {

        GenerateProductCommand generateProduct = new GenerateProductCommand();

        Options options = new Options();
        options.addOption(
            Option.builder("u")
                .desc("Database JDBC URL. Required.")
                .hasArg()
                .argName("DATABASE-URL")
                .required()
                .build()
        );

        options.addOption(
            Option.builder("o")
                .desc("Output directory for generated files. Required.")
                .hasArg()
                .argName("OUTPUT-DIR")
                .required()
                .build()
        );

        options.addOption(
            Option.builder("c")
                .desc("Number of products to generate. Required.")
                .hasArg()
                .argName("PRODUCTS-COUNT")
                .required()
                .build()
        );

        options.addOption(
            Option.builder("p")
                .desc("Elasticsearch Product Index name. Defaults to '" + defaultProductIndex + "'.")
                .hasArg()
                .build()
        );

        options.addOption(
            Option.builder("m")
                .desc("Elasticsearch Product and Product Model Index name. Defaults to '" + defaultProductAndProductModelIndex + "'.")
                .hasArg()
                .build()
        );

        CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = null;

        try {
            cmd = cliParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.err.println();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "<akeneo-product-generator-full-jar-file>", options );
            System.exit(1);
        }

        String databaseUrl = cmd.getOptionValue("u");
        String outputDirectory = cmd.getOptionValue("o");
        int productCount = Integer.valueOf(cmd.getOptionValue("c"));

        String productIndex = cmd.getOptionValue("p", defaultProductIndex);
        String productAndProductModelIndex = cmd.getOptionValue("m", defaultProductAndProductModelIndex);

        System.out.format("Generating %d products in %s directory...", productCount, outputDirectory);

        generateProduct.execute(
            databaseUrl,
            outputDirectory,
            productCount,
            productIndex,
            productAndProductModelIndex
        );

        System.out.println("Done.");
    }
}
