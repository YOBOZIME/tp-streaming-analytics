package org.example.tpstreaminganalytics;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/v1/analytics")
public class HelloApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(org.example.tpstreaminganalytics.api.AnalyticsResource.class);
        classes.add(org.example.tpstreaminganalytics.api.SSEResource.class);
        classes.add(org.example.tpstreaminganalytics.config.JacksonConfig.class);
        return classes;
    }
}