package org.harsh.features.authentication.resource;

import org.harsh.features.authentication.service.AuthService;
import org.harsh.domain.AuthInfo;
import org.harsh.domain.LoginBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Login {
    @POST
    public Response createAuthSession(LoginBody body) {
        AuthService service = new AuthService();
        try {
            AuthInfo info = service.createAuthSession(body);
            if (info == null) {
                throw new WebApplicationException("User Not Found", Response.Status.NOT_FOUND);
            }
            return Response.ok().entity(info).build();
        } catch (Exception e) {
            return Response.status(400).entity(e.getMessage()).build();
        }

        // receive email and pwd
        // create encrypted pwd
        // fetch encrypted pwd from the db
        // compare both
        // if true,
        // if access token is expired, create new, else return the same access token
    }
}
