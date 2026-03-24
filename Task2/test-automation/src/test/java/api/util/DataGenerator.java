package api.util;

/*
 * Класс содержит методы для генерации случайных данных
 */

import api.Constants;

import java.util.Random;

public final class DataGenerator {

    private DataGenerator() {
    }

    public static Integer generateValidSellerId() {
        return new Random().nextInt(Constants.MIN_SELLER_ID, Constants.MAX_SELLER_ID + 1);
    }

    public static Long generateValidPrice() {
        return new Random().nextLong(Constants.MIN_VALID_PRICE, Constants.MAX_VALID_PRICE + 1);
    }

    public static String generateValidName() {
        String[] names = new String[]{
                "auto", "clock", "phone", "laptop", "tablet",
                "watch", "camera", "headphones", "monitor", "keyboard"
        };
        Random random = new Random();
        return names[random.nextInt(names.length)];
    }

    public static Long generateValidStatisticsField() {
        return new Random()
                .nextLong(Constants.MIN_VALID_STATISTICS_FIELD, Constants.MAX_VALID_STATISTICS_FIELD + 1);

    }
}
