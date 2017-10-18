package bitone.akeneo.product_generator.domain;

import java.util.Random;
import java.util.Date;
import com.github.javafaker.Faker;

public class RandomlyPicker {
    private static Random rand = new Random();
    private static Faker faker = new Faker();

    public static Integer pickArrayIndex(int arrayLength) {
        if (arrayLength == 0) {
            return null;
        } else if (arrayLength == 1) {
            return 0;
        } else {
            return rand.nextInt(arrayLength - 1);
        }
    }

    public static boolean pickBoolean() {
        return rand.nextBoolean();
    }

    public static int pickIntBetween(int min, int max) {
        return rand.nextInt(max - min) + min;
    }

    public static Date pickDateBetween(Date min, Date max) {
        return faker.date().between(min, max);
    }
}
