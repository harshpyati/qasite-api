package org.harsh.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.harsh.domain.AuthInfo;
import org.harsh.domain.LoginBody;
import org.harsh.domain.UserInfo;
import org.harsh.daos.UserDao;
import org.harsh.utils.db.DBUtils;
import org.harsh.utils.ValidationUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.harsh.utils.ValidationUtils.isNotNull;
import static org.harsh.utils.ValidationUtils.isNull;

@Slf4j
public class UserService {
    private final UserDao userDao;

    public UserService() {
        userDao = new UserDao();
    }

    private void validateAdd(UserInfo userInfo){
        if (ValidationUtils.isNullOrEmpty(userInfo.getPwd())) {
            throw new WebApplicationException("Enter a valid password", Response.Status.BAD_REQUEST);
        }
    }

    private void validateUserInfo(UserInfo userInfo){
        if (ValidationUtils.isNullOrEmpty(userInfo.getName())) {
            throw new WebApplicationException("Enter a valid name", Response.Status.BAD_REQUEST);
        }

        if (ValidationUtils.isNullOrEmpty(userInfo.getEmail())) {
            throw new WebApplicationException("Enter a valid email", Response.Status.BAD_REQUEST);
        }

        if (ValidationUtils.isNullOrEmpty(userInfo.getDob())) {
            throw new WebApplicationException("Date of Birth field cannot be empty", Response.Status.BAD_REQUEST);
        }

        SimpleDateFormat dobFormat = new SimpleDateFormat("dd/MM/yyyy");
        dobFormat.setLenient(false);

        try {
            dobFormat.parse(userInfo.getDob());
        } catch (ParseException pe) {
            throw new WebApplicationException("Enter a valid date of birth", Response.Status.BAD_REQUEST);
        }
    }

    public Response createUser(UserInfo user) {
        long id;
        try {
            validateUserInfo(user);
            validateAdd(user);
            String encryptedPwd = DigestUtils.md5Hex(user.getPwd());
            user.setEncryptedPwd(encryptedPwd);
            user.setCreatedAt(System.currentTimeMillis());
            id = userDao.insertUserDetails(user);
            if (id != -1) {
                user = userDao.getUserDetails(id);
                return Response.ok().entity(user).build();
            }else {
                throw new WebApplicationException("Failed to insert user details",Response.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Response getUserById(long id) {
        try {
            UserInfo user = userDao.getUserDetails(id);
            if (isNull(user)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return Response.ok().entity(user).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Response deleteUser(int id) {
        try {
            UserInfo user = userDao.getUserDetails(id);
            if (isNull(user)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return userDao.deleteUser(id);
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Response updateUser(int id, UserInfo user) {
        try {
            UserInfo dbUser = userDao.getUserDetails(id);
            if (isNull(dbUser)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                validateUserInfo(user);
                dbUser.setName(user.getName());
                dbUser.setEmail(user.getEmail());
                dbUser.setDob(user.getDob());
                user = userDao.updateUser(dbUser);
                return Response.status(200).entity(user).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Response createAuthSession(LoginBody body) throws Exception {
        String email = body.getEmail();
        String pwd = body.getPassword();
        String encPwd = pwd != null ? DigestUtils.md5Hex(pwd) : null;
        if (isNull(encPwd)) {
            throw new WebApplicationException("Failed to Authenticate", Response.Status.NOT_FOUND);
        }

        AuthInfo userInfo = userDao.checkIfUserExists(email, encPwd);
        if (isNotNull(userInfo)) {
            // retrieve access-token
            long tokenGap = System.currentTimeMillis() - userInfo.getAccessTokenTime();
            if (tokenGap > ValidationUtils.TIME_BETWEEN_LOGIN) {
                // expired
                System.out.println("Issuing New access token");
                String newAccessToken = DBUtils.generateAccessToken();
                userInfo.setAccessToken(newAccessToken);
                long accessTokenTime = System.currentTimeMillis();
                userInfo.setAccessTokenTime(accessTokenTime);
                // update auth details
                userDao.updateAuthDetails(userInfo.getId(), newAccessToken, accessTokenTime);
            }
            return Response.ok().entity(userInfo).build();
        }
        // check against the auth-details table and verify if email-encPwd entry exists
        // if it exists, check if the access-token has expired
        // if it has expired, create a new access-token
        return null;
    }

    public boolean validateToken(String token) throws Exception {
        String sql = "select access_token_time from authdetails where access_token = '" + token + "'";
        System.out.println("SQL To validate the token: " + sql);
        Long time = null;
        try {
            Connection conn = DBUtils.getDBConnection();
            Statement stmnt = conn.createStatement();
            ResultSet rs = stmnt.executeQuery(sql);
            while (rs.next()) {
                time = rs.getLong("access_token_time");
            }

            return time != null;
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
