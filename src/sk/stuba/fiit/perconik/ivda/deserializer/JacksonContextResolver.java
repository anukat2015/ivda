package sk.stuba.fiit.perconik.ivda.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by Seky on 20. 7. 2014.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JacksonContextResolver implements ContextResolver<ObjectMapper> {

    private ObjectMapper mapper;

    public JacksonContextResolver() throws Exception {
        mapper = new ObjectMapper();
        mapper.registerModule(new EventDeserializerModule());
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.PASCAL_CASE_TO_CAMEL_CASE);
    }

    @Override
    public ObjectMapper getContext(Class<?> objectType) {
        return mapper;
    }
}
