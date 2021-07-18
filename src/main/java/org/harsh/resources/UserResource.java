package org.harsh.resources;

import org.harsh.domain.LoginBody;
import org.harsh.domain.UserInfo;
import org.harsh.services.UserService;
import org.harsh.filters.annotations.Secured;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    UserService service = new UserService();

    @POST
    @Path("/signup")
    public Response createUser(UserInfo user) {
        return service.createUser(user);
    }

    @POST
    @Path("/login")
    public Response createAuthSession(LoginBody body) {
        try {
            Response info = service.createAuthSession(body);
            if (info == null) {
                throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
            }
            return info;
        } catch (Exception e) {
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteUser(@PathParam("id") int id) {
        return service.deleteUser(id);
    }

    @PUT
    @Path("/{id}")
    @Secured
    public Response updateUser(@PathParam("id") int id, UserInfo user) {
        return service.updateUser(id, user);
    }

    @GET
    @Path("/{id}")
    @Secured
    public Response getUser(@PathParam("id") int id, @Context ServletContext context) {
        return service.getUserById(id);
    }

    @GET
    @Path("/validate")
    public Response validateUser(@QueryParam("token") String token) {
        try {
            if (token == null){
                return Response.status(400).build();
            }
            boolean flag = service.validateToken(token);
            if (flag) {
                return Response.ok().build();
            } else {
                return Response.status(400).entity("User Not Found.").build();
            }
        } catch (Exception e) {
            return Response.status(400).build();
        }
    }
}
