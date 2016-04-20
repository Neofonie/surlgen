package de.neofonie.surlgen.processor.core;

public class CamelCaseUtils {

    public static String firstCharLowerCased(String input) {
        if (input == null) {
            return null;
        }
        if (input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toLowerCase() + input.substring(1);
    }

    public static String firstCharUpperCased(String input) {
        if (input == null) {
            return null;
        }
        if (input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
