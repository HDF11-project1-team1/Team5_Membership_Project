package common.validation;

public class InputValidator {

    private InputValidator() {
    }

    public static boolean isValidId(int id) {
        return id > 0;
    }

    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public static boolean isValidRate(int rate) {
        return rate >= 0 && rate <= 100;
    }

    public static boolean isValidRate(double rate) {
        return rate >= 0.0 && rate <= 1.0;
    }

    public static boolean isValidMileageRate(double rate) {
        return rate >= 0.0 && rate <= 1.0;
    }

    public static boolean isValidMileageRate(int rate) {
        return rate >= 0 && rate <= 1;
    }

    public static boolean isValidAmount(int amount) {
        return amount >= 0;
    }

    public static boolean isValidStandardRange(int minStandard, int maxStandard) {
        return minStandard >= 0 && maxStandard >= minStandard;
    }
}
