package org.harsh.features.resources;

import org.harsh.features.domain.UserInfo;
import org.harsh.features.services.AuthService;
import org.harsh.filters.annotations.Secured;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DetailsResource {
    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteUser(@PathParam("id") int id) {
        AuthService service = new AuthService();
        return service.deleteUser(id);
    }

    @PUT
    @Path("/{id}")
    @Secured
    public Response updateUser(@PathParam("id") int id, UserInfo user) {
        AuthService service = new AuthService();
        return service.updateUser(id, user);
    }

    @GET
    @Path("/{id}")
    @Secured
    public Response getUser(@PathParam("id") int id, @Context ServletContext context) {
        AuthService service = new AuthService();
        return service.getUserById(id);
    }

    @GET
    @Path("/validate")
    public Response validateUser(@QueryParam("token") String token) {
        try {
            AuthService srvc = new AuthService();
            if (token == null){
                return Response.status(400).build();
            }
            boolean flag = srvc.validateToken(token);
            if (flag) {
                return Response.ok().build();
            } else {
                return Response.status(400).build();
            }
        } catch (Exception e) {
            return Response.status(400).build();
        }
    }

    @POST
    @Path("/validate")
    public Response validateUserPost(UserInfo user) {
        try {
            AuthService srvc = new AuthService();
            if (user.getAccessToken() == null){
                return Response.status(400).header("Access-Control-Allow-Origin","http://localhost:3000").build();
            }
            boolean flag = srvc.validateToken(user.getAccessToken());
            if (flag) {
                return Response.ok().header("Access-Control-Allow-Origin","http://localhost:3000").build();
            } else {
                return Response.status(400).header("Access-Control-Allow-Origin","http://localhost:3000").build();
            }
        } catch (Exception e) {
            return Response.status(400).header("Access-Control-Allow-Origin","http://localhost:3000").build();
        }
    }
}
