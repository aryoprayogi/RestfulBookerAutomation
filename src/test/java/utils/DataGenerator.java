package utils;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class DataGenerator {
    private static final Faker faker = new Faker();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static String getRandomFirstName(){
        return faker.name().firstName();
    }

    public static String getRandomLastName(){
        return faker.name().lastName();
    }

    public static int getRandomPrice(){
        return faker.number().numberBetween(100, 999);
    }

    public static boolean getRandomDepositPaid(){
        return faker.random().nextBoolean();
    }

    public static String getRandomAdditionalNeeds(){
        return faker.options().option("Breakfast", "Late Checkout", "Extra Bed", "No Pork");
    }

    public static String getRandomCheckinDate(){
        //Maksimal undur 5 hari dari hari ini
        return sdf.format(faker.date().past(5, TimeUnit.DAYS));
    }

    public static String getRandomCheckoutDate() {
        //Paling cepat 5 hari dari sekarang, dan paling lama 15 hari dari sekarang.
        return sdf.format(faker.date().future(15, 5, TimeUnit.DAYS));
    }

}
