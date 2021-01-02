package org.harsh.authentication.dao;

import org.harsh.domain.UserInfo;
import org.harsh.utils.DBUtils;

import javax.ws.rs.core.Response;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AuthDao {
    public int insertUserDetails(UserInfo user) throws Exception {
        DateFormat dobFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date userDob = dobFormat.parse(user.getDob());
        String sql = "insert into users (name, email,dob,gender) values ('"
                + user.getName() + "','" + user.getEmail() + "','" + userDob + "','"
                + user.getGender() + "');";
        int id = -1;
        System.out.println("SQl: " + sql);
        try (Connection dbConnection = DBUtils.getDBConnection();
             PreparedStatement statement = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int rows = statement.executeUpdate();
            System.out.println("Num Rows: " + rows);
            if (rows == 1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                    System.out.println("Id:" + id);
                    // this id is the id in the user details
                    // use this id and fill the authdetails table
                    String accessToken = DBUtils.generateAccessToken();
                    System.out.println("Access Token: " + accessToken);
                    Calendar cal = Calendar.getInstance();
                    Timestamp tsmp = new Timestamp(cal.getTimeInMillis());
                    String authSql = "insert into authdetails(id, email, password, access_token" +
                            ", access_token_time) values (" + id + ",'" + user.getEmail() + "','"
                            + user.getEncryptedPwd() + "','" + accessToken + "','" + tsmp + "');";
                    try (PreparedStatement stmnt = dbConnection.prepareStatement(authSql)) {
                        int authRows = stmnt.executeUpdate();
                        System.out.println("Auth Rows: " + authRows);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
        System.out.println("Id Returning is: " + id);
        return id;
    }

    public UserInfo getUserDetails(int id) throws Exception {
        String sql = "select * from users where id = " + id + ";";
        System.out.println("Sql: Fetch" + sql);
        UserInfo userInfo = new UserInfo();
        try (Connection dbConnection = DBUtils.getDBConnection();
             Statement statement = dbConnection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                userInfo.setId(rs.getInt("id"));
                userInfo.setName(rs.getString("name"));
                userInfo.setEmail(rs.getString("email"));
                Date dbDob = rs.getDate("dob");
                userInfo.setDob(dbDob.toString());
                userInfo.setGender(rs.getString("gender"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }
        return userInfo;
    }

    public Response deleteUser(int id) throws Exception {
        String sql = "delete from users where id = " + id + ";";
        try (Connection connection = DBUtils.getDBConnection();
             Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);
            return Response.noContent().build();
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public UserInfo updateUser(UserInfo userInfo) throws Exception {
        String sql = "update users set";
        try (Connection connection = DBUtils.getDBConnection(); Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(sql);
            return getUserDetails(userInfo.getId());
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public boolean checkIfUserExists(String email, String pwd) throws Exception {
        int userCount = 0;
        String sql = "select * from authdetails where email = " + email + " and password = " + pwd + ";";
        try (Connection connection = DBUtils.getDBConnection(); Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            ResultSet rs = statement.executeQuery(sql);
            userCount = rs.getInt(1);
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }

        return userCount != 0;
    }
}
