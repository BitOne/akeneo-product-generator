package bitone.akeneo.product_generator.domain;

import java.util.Random;
import java.util.Date;
import com.github.javafaker.Faker;

public class RandomlyPicker {
    private static RandomlyPicker picker;
    private static long seed = 0;

    private Random rand;
    private Faker faker;

    private RandomlyPicker() {
        if (0 == seed) {
            rand = new Random();
        } else {
            rand = new Random(seed);
        }
        this.faker = new Faker(rand);
    }

    public static void setSeed(long seed) {
        RandomlyPicker.seed = seed;
    }

    public static RandomlyPicker getInstance() {
        if (null == picker) {
            picker = new RandomlyPicker();
        }

        return picker;
    }

    public Integer pickArrayIndex(int arrayLength) {
        if (arrayLength == 0) {
            return null;
        } else if (arrayLength == 1) {
            return 0;
        } else {
            return rand.nextInt(arrayLength - 1);
        }
    }

    public boolean pickBoolean() {
        return rand.nextBoolean();
    }

    public int pickIntBetween(int min, int max) {
        return rand.nextInt(max - min) + min;
    }

    public Date pickDateBetween(Date min, Date max) {
        return faker.date().between(min, max);
    }

    public String pickLongText() {
        return faker.shakespeare().hamletQuote();
    }

    public String pickShortText() {
        return faker.book().title();
    }
}
