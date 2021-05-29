package org.harsh.features.resources;

import org.harsh.features.domain.UserInfo;
import org.harsh.features.services.AuthService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/signup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SignupResource {
    @POST
    public Response createUser(UserInfo user) {
        AuthService service = new AuthService();
        return service.createUser(user);
    }
}
