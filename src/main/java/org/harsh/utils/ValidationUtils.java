package org.harsh.utils;

import java.util.Collection;
import java.util.Random;

public class ValidationUtils {
    public static long TIME_BETWEEN_LOGIN = 3600000L;
    public static int LENGTH_USERNAME = 16;
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }
    public static boolean isNotNull(Object obj){
        return obj != null;
    }

    public static String generateRandomUserName(int length){
        return generateUserNameOfLength(length);
    }

    public static String generateRandomUserName(){
        return generateUserNameOfLength(LENGTH_USERNAME);
    }

    private static String generateUserNameOfLength(int length){
        StringBuilder userName = new StringBuilder();
        Random random = new Random();

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        for (int i = 0; i < length; i++){
            int index = random.nextInt(26);
            userName.append(alphabet[index]);
        }

        return userName.toString();
    }
}
