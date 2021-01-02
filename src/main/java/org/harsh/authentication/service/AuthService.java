package org.harsh.authentication.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.harsh.domain.LoginBody;
import org.harsh.domain.UserInfo;
import org.harsh.authentication.dao.AuthDao;
import org.harsh.authentication.exception.ApiException;
import org.harsh.authentication.exception.ErrorCodes;
import org.harsh.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AuthService {
    private final AuthDao authDao;
    private static Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService() {
        authDao = new AuthDao();
    }

    private void validateUserInfo(UserInfo userInfo) throws ApiException {
        System.out.println(userInfo.toString());
        if (ValidationUtils.isNullOrEmpty(userInfo.getName())) {
            throw new WebApplicationException("Enter a valid name", Response.Status.BAD_REQUEST);
        }

        if (ValidationUtils.isNullOrEmpty(userInfo.getPwd())) {
            throw new WebApplicationException("Enter a valid password", Response.Status.BAD_REQUEST);
        }

        if (ValidationUtils.isNullOrEmpty(userInfo.getEmail())) {
            throw new WebApplicationException("Enter a valid email", Response.Status.BAD_REQUEST);
        }
        System.out.println("Date Of Birth:" + userInfo.getDob());
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
        int id;
        try {
            validateUserInfo(user);
            String encryptedPwd = DigestUtils.md5Hex(user.getPwd());
            user.setEncryptedPwd(encryptedPwd);
            id = authDao.insertUserDetails(user);
            if (id != -1) {
                user = authDao.getUserDetails(id);
                return Response.ok().entity(user).build();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return null;
    }

    public Response getUserById(int id) {
        try {
            UserInfo user = authDao.getUserDetails(id);
            if (user == null) {
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
            UserInfo user = authDao.getUserDetails(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                return authDao.deleteUser(id);
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Response updateUser(int id, UserInfo user) {
        try {
            UserInfo dbUser = authDao.getUserDetails(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                validateUserInfo(user);
                user.setId(dbUser.getId());
                user = authDao.updateUser(user);
                return Response.status(200).entity(user).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public UserInfo createAuthSession(LoginBody body) throws Exception {
        String email = body.getEmail();
        String pwd = body.getPassword();
        String encPwd = pwd != null ? DigestUtils.md5Hex(pwd) : null;
        if (encPwd == null) {
            throw new ApiException("Failed to Authenticate", ErrorCodes.INVALID_ARGUMENT);
        }

        boolean userExists = authDao.checkIfUserExists(email, encPwd);
        if (userExists) {
            // retrieve access-token

        }
        // check against the auth-details table and verify if email-encPwd entry exists
        // if it exists, check if the access-token has expired
        // if it has expired, create a new access-token
        return new UserInfo();
    }
}
