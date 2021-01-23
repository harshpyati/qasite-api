package org.harsh.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        System.out.println("Checking if it is a preflight request");
        if (isPreflightRequest(containerRequestContext)) {
            containerRequestContext.abortWith(Response.ok().build());
            return;
        }
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        // if there is no Origin header, then it is not a
        // cross origin request. We don't do anything.
        System.out.println("filter impl");
        if (containerRequestContext.getHeaderString("Origin") == null) {
            return;
        }

        // If it is a preflight request, then we add all
        // the CORS headers here.
        if (isPreflightRequest(containerRequestContext)) {
            containerResponseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
            containerResponseContext.getHeaders().add("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            containerResponseContext.getHeaders().add("Access-Control-Allow-Headers",
                    // Whatever other non-standard/safe headers (see list above)
                    // you want the client to be able to send to the server,
                    // put it in this list. And remove the ones you don't want.
                    "X-Requested-With, Authorization, " +
                            "Accept-Version, Content-MD5, CSRF-Token, Content-Type");
        }

        // Cross origin requests can be either simple requests
        // or preflight request. We need to add this header
        // to both type of requests. Only preflight requests
        // need the previously added headers.
        containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
    }

    private static boolean isPreflightRequest(ContainerRequestContext requestContext) {
        return requestContext.getHeaderString("Origin") != null && requestContext.getMethod().equalsIgnoreCase("OPTIONS");
    }


}
