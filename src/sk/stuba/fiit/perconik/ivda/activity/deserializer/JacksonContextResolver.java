package sk.stuba.fiit.perconik.ivda.activity.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by Seky on 20. 7. 2014.
 * <p>
 * Upraveny JacksonContextResolver, kde registrujeme pre Jackson vlastny modul.
 * Zaroven sa tu nastavuju nastavenia Jacksonu.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class JacksonContextResolver implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public JacksonContextResolver() {
        mapper = new ObjectMapper();
        mapper.registerModule(new EventDeserializerModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
