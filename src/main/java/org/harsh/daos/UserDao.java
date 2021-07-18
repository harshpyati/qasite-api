package org.harsh.daos;

import lombok.extern.slf4j.Slf4j;
import org.harsh.domain.AuthInfo;
import org.harsh.domain.UserInfo;
import org.harsh.utils.db.DBUtils;

import javax.ws.rs.core.Response;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Slf4j
public class UserDao {
    public long insertUserDetails(UserInfo user) throws Exception {
        DateFormat dobFormat = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date userDob = dobFormat.parse(user.getDob());
        String sql = "insert into users (name, email,dob, created_at) values ('"
                + user.getName() + "','" + user.getEmail() + "','" + userDob + "',"
                + user.getCreatedAt() + ");";
        long id = -1;
        System.out.println("SQl: " + sql);
        try (Connection dbConnection = DBUtils.getDBConnection();
             PreparedStatement statement = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int rows = statement.executeUpdate();
            System.out.println("Num Rows: " + rows);
            if (rows == 1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getLong(1);
                    System.out.println("Id:" + id);
                    // this id is the id in the user details
                    // use this id and fill the authdetails table
                    String accessToken = DBUtils.generateAccessToken();
                    System.out.println("Access Token: " + accessToken);
                    String authSql = "insert into authdetails(id, email, password, access_token" +
                            ", access_token_time) values (" + id + ",'" + user.getEmail() + "','"
                            + user.getEncryptedPwd() + "','" + accessToken + "','" + System.currentTimeMillis() + "');";
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

    public UserInfo getUserDetails(long id) throws Exception {
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
                userInfo.setVerified(rs.getBoolean("verified"));
                userInfo.setCreatedAt(rs.getLong("created_at"));
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

            String deleteFromAuthDetails = "delete from authdetails where id = " + id + ";";
            int rows = statement.executeUpdate(deleteFromAuthDetails);
            System.out.println("rows: " + rows);
            return Response.noContent().build();
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public UserInfo updateUser(UserInfo userInfo) throws Exception {
        String sql = "update users set name = '" + userInfo.getName() +"', email='" + userInfo.getEmail() +"' where id = " + userInfo.getId() + ";";
        System.out.println("update sql: " + sql);
        try (Connection connection = DBUtils.getDBConnection(); Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(sql);
            return getUserDetails(userInfo.getId());
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public AuthInfo checkIfUserExists(String email, String pwd) throws Exception {
        AuthInfo info = null;
        String sql = "select id, email, access_token, access_token_time from authdetails where email = '" + email + "' and password = '" + pwd + "';";
        try (
                Connection connection = DBUtils.getDBConnection();
                Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                info = new AuthInfo();
                info.setId(rs.getInt("id"));
                info.setEmail(rs.getString("email"));
                info.setAccessToken(rs.getString("access_token"));
                info.setAccessTokenTime(rs.getLong("access_token_time"));
            }
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
        return info;
    }

    public void updateAuthDetails(int id, String accessToken, long time) {
        String sql = "update authdetails set access_token = '" + accessToken + "',access_token_time='" + time + "' where id = " + id + ";";
        try (Connection conn = DBUtils.getDBConnection(); Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}