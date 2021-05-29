package org.harsh.utils;

import java.util.Collection;

public class ValidationUtils {
    public static long TIME_BETWEEN_LOGIN = 3600000L;
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }
    public static boolean isNull(Object obj) {
        return obj == null;
    }
    public static boolean isNotNull(Object obj){
        return obj != null;
    }
}
