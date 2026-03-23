package api.util;

public class CommonMethods {
    public static String getIdFromStatus(String status) {
        return status.replace("Сохранили объявление - ", "");
    }
}
