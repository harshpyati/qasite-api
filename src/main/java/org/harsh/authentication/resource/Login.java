package org.harsh.authentication.resource;

import org.harsh.authentication.service.AuthService;
import org.harsh.domain.LoginBody;
import org.harsh.domain.UserInfo;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Login {
    @POST
    public UserInfo createAuthSession(@Context ServletContext context, LoginBody body) {
        String email = body.getEmail();
        String password = body.getPassword();
        AuthService srvc = new AuthService();
        try {
            return srvc.createAuthSession(body);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        // receive email and pwd
        // create encrypted pwd
        // fetch encrypted pwd from the db
        // compare both
        // if true, return access token
    }
}
