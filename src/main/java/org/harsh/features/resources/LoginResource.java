package org.harsh.features.resources;

import org.harsh.features.services.AuthService;
import org.harsh.features.domain.AuthInfo;
import org.harsh.features.domain.LoginBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

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
    }
}
