package org.harsh.authentication.resource;

import org.harsh.domain.UserInfo;
import org.harsh.authentication.service.AuthService;
import org.harsh.utils.DBUtils;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;

@Path("/user")
public class Details {
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int id) {
        AuthService service = new AuthService();
        return service.deleteUser(id);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") int id, UserInfo user) {
        AuthService service = new AuthService();
        return service.updateUser(id, user);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("id") int id, @Context ServletContext context) {
        AuthService service = new AuthService();
        return service.getUserById(id);
    }
}
