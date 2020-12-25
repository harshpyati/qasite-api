package org.harsh.authentication.resource;

import org.harsh.domain.UserInfo;
import org.harsh.authentication.service.AuthService;
import org.harsh.utils.DBUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.sql.Connection;

@Path("/user")
public class Details {
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id, @Context ServletContext context) {
        Connection connection = (Connection) context.getAttribute(DBUtils.DB_CONTEXT);
        AuthService service = new AuthService(connection);
        return service.deleteUser(id);
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") int id, @Context ServletContext context, UserInfo user) {
        Connection connection = (Connection) context.getAttribute(DBUtils.DB_CONTEXT);
        AuthService service = new AuthService(connection);
        return service.updateUser(id, user);
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") int id, @Context ServletContext context) {
        Connection dbConnection = (Connection) context.getAttribute(DBUtils.DB_CONTEXT);
        AuthService service = new AuthService(dbConnection);
        return service.getUserById(id);
    }
}
