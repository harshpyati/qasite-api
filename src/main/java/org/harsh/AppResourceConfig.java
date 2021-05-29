package org.harsh;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.harsh.features.resources.DetailsResource;
import org.harsh.filters.CorsFilter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppResourceConfig extends ResourceConfig {
    private void ApplicationInit(){
        packages("org.harsh");
        register(DetailsResource.class);
        register(CorsFilter.class);
    }
    public AppResourceConfig() {
        ApplicationInit();
        register(new LoggingFeature(
                Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
                Level.INFO,
                LoggingFeature.Verbosity.PAYLOAD_ANY,
                10000
        ));
    }
}
