package org.harsh.utils;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;

public class DBUtils {
    public static String DB_URL = "jdbc:postgresql://localhost/qasite";
    public static String DB_UNAME = "postgres";
    public static String DB_PWD = "admin@harsh";
    public static String DB_CONTEXT = "DB_CONNECTION";

    public static String generateAccessToken() {
        SecureRandom secRandom = new SecureRandom();
        Base64.Encoder base64Enc = Base64.getUrlEncoder();

        byte[] randomBytes = new byte[20];
        secRandom.nextBytes(randomBytes);
        return base64Enc.encodeToString(randomBytes);
    }

    public static Connection getDBConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Driver version: " + org.postgresql.Driver.getVersion());
        return DriverManager.getConnection(
                DB_URL,
                DB_UNAME,
                DB_PWD
        );

    }
}
