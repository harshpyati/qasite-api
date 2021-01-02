package org.harsh.authentication.resource;

import org.harsh.domain.UserInfo;
import org.harsh.authentication.service.AuthService;
import org.harsh.utils.DBUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;

@Path("/signup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Signup {
    @POST
    public Response createUser(UserInfo user) {
        AuthService service = new AuthService();
        return service.createUser(user);
    }
}
