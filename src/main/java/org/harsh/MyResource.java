package org.harsh;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/test")
@Consumes(MediaType.APPLICATION_JSON)
public class MyResource {

    @GET
    public String getIt() {
        return "Got it bruh!!";
    }
}
