package org.harsh.authentication.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.harsh.domain.UserInfo;
import org.harsh.authentication.dao.AuthDao;
import org.harsh.authentication.exception.ApiException;
import org.harsh.authentication.exception.ErrorCodes;
import org.harsh.utils.ValidationUtils;

import javax.ws.rs.core.Response;
import java.sql.Connection;

public class AuthService {
    private AuthDao authDao;

    public AuthService(Connection connection) {
        authDao = new AuthDao(connection);
    }

    private void validateUserInfo(UserInfo userInfo) throws ApiException {
        if (ValidationUtils.isNullOrEmpty(userInfo.getName())) {
            throw new ApiException("Enter a valid name", ErrorCodes.INVALID_ARGUMENT);
        }

        if (ValidationUtils.isNullOrEmpty(userInfo.getPwd())) {
            throw new ApiException("Enter a valid password", ErrorCodes.INVALID_ARGUMENT);
        }

        if (ValidationUtils.isNullOrEmpty(userInfo.getEmail())) {
            throw new ApiException("Enter a valid email", ErrorCodes.INVALID_ARGUMENT);
        }

        if (ValidationUtils.isNull(userInfo.getDob())) {
            throw new ApiException("Enter a valid date of birth", ErrorCodes.INVALID_ARGUMENT);
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
}
