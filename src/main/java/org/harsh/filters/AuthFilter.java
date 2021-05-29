package org.harsh.filters;

import org.harsh.filters.annotations.Secured;
import org.harsh.utils.db.DBUtils;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {
    private static final String AUTH_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String authHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.toLowerCase().startsWith(AUTH_SCHEME.toLowerCase() + " ")) {
            containerRequestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .build()
            );
        }

        // validate token, simply check if the access token exists in the auth details table, if yes, retrieve the details
        // if access token is validated, then return;
        // else:- throw an unauthorized exception

        String accessToken = null;
        if (authHeader != null) {
            accessToken = authHeader.substring(AUTH_SCHEME.length()).trim();
        }else {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
        try {
            if (!validateToken(accessToken)) {
                containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateToken(String token) throws Exception {
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
