package org.harsh.authentication.dao;

import org.harsh.domain.UserInfo;

import javax.ws.rs.core.Response;
import java.sql.*;

public class AuthDao {
    private Connection connection;

    public AuthDao(Connection connection) {
        this.connection = connection;
    }

    public int insertUserDetails(UserInfo user) throws Exception {
        String sql = "insert into users (name, encrypted_pwd,email,date_of_birth,gender,access_token) values ("
                + user.getName() + "," + user.getEncryptedPwd() + "," + user.getEmail() + "," + user.getDob() + ","
                + user.isGender() + "," + user.getAccessToken() + ");";
        int id = -1;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int rows = statement.executeUpdate();
            if (rows > 1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new Exception();
        }

        return id;
    }

    public UserInfo getUserDetails(int id) throws Exception {
        String sql = "select * from users where id = " + id + ";";
        UserInfo userInfo = new UserInfo();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            userInfo.setId(rs.getInt("id"));
            userInfo.setName(rs.getString("name"));
            userInfo.setEncryptedPwd(rs.getString("encrypted_pwd"));
            userInfo.setEmail(rs.getString("email"));
            userInfo.setDob(rs.getDate("date_of_birth"));
            userInfo.setGender(rs.getBoolean("gender"));
            userInfo.setAccessToken(rs.getString("access_token"));
        } catch (SQLException ex) {
            throw new Exception();
        }
        return userInfo;
    }

    public Response deleteUser(int id) throws Exception {
        String sql = "delete from users where id = " + id + ";";
        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);
            return Response.noContent().build();
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public UserInfo updateUser(UserInfo userInfo) throws Exception {
        String sql = "update users set";
        try (Statement statement = connection.createStatement()) {
            int rows = statement.executeUpdate(sql);
            return getUserDetails(userInfo.getId());
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
