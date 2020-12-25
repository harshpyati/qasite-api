package org.harsh.authentication.resource;

import javax.servlet.ServletContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/login")
public class Login {
    @POST
    public void createAuthSession(@Context ServletContext context){
        // receive email and pwd
        // create encrypted pwd
        // fetch encrypted pwd from the db
        // compare both
        // if true, return access token
    }
}
