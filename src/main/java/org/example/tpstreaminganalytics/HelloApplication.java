package org.example.tpstreaminganalytics;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import org.example.tpstreaminganalytics.api.AnalyticsResource;
import org.example.tpstreaminganalytics.api.SSEResource;
import org.example.tpstreaminganalytics.config.JacksonConfig;

/**
 * JAX-RS Application configuration for Jersey
 * Base path: /api
 */
@ApplicationPath("/api")
public class HelloApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        // Register JAX-RS resources
        classes.add(AnalyticsResource.class);
        classes.add(SSEResource.class);
        // Register providers
        classes.add(JacksonConfig.class);
        return classes;
    }
}