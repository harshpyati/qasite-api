package org.harsh.authentication.resource;

import org.harsh.domain.UserInfo;
import org.harsh.authentication.service.AuthService;
import org.harsh.utils.DBUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.Connection;

@Path("/signup")
public class Signup {
    @POST
    public Response createUser(@Context ServletContext context, UserInfo user) {
        Connection dbConnection = (Connection) context.getAttribute(DBUtils.DB_CONTEXT);
        AuthService service = new AuthService(dbConnection);
        return service.createUser(user);
    }
}
