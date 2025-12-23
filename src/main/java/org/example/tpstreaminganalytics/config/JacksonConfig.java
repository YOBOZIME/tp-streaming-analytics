package org.example.tpstreaminganalytics.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Jackson ObjectMapper configuration for JAX-RS
 * Enables proper serialization of Java 8 date/time types (LocalDateTime, etc.)
 */
@Provider
@ApplicationScoped
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper objectMapper;

    public JacksonConfig() {
        this.objectMapper = new ObjectMapper();
        // Register Java 8 date/time module
        this.objectMapper.registerModule(new JavaTimeModule());
        // Configure date serialization as strings instead of timestamps
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }

    @Produces
    @ApplicationScoped
    public ObjectMapper createObjectMapper() {
        return objectMapper;
    }
}
