package org.harsh.utils;

public class ValidationUtils {
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static boolean isNull(Object input) {
        System.out.println(input == null);
        return input == null;
    }
}
