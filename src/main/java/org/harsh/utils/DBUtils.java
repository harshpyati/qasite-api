package org.harsh.utils;

import org.harsh.domain.UserInfo;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.security.SecureRandom;
import java.sql.*;
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

    public static UserInfo getUserDetails(String accessToken) throws SQLException {
        UserInfo currentUser = null;
        String sql = "select id,email from authdetails where access_token='" + accessToken + "';";
        try (Connection connection = getDBConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                currentUser = new UserInfo();
                currentUser.setId(rs.getInt("id"));
                currentUser.setEmail(rs.getString("email"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new WebApplicationException(ex.getMessage());
        }
        return currentUser;
    }

    public static String getAccessToken(ContainerRequestContext context){
        return context.getHeaderString(HttpHeaders.AUTHORIZATION).substring("Bearer".length()).trim();
    }
}
